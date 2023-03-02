package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.Basket;
import com.powernode.domain.Sku;
import com.powernode.feign.BasketSkuFeign;
import com.powernode.mapper.BasketMapper;
import com.powernode.model.CartItem;
import com.powernode.model.ShopCart;
import com.powernode.service.BasketService;
import com.powernode.vo.CartTotalAmount;
import com.powernode.vo.CartVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket> implements BasketService{

    @Autowired
    private BasketMapper basketMapper;

    @Autowired
    private BasketSkuFeign basketSkuFeign;

    @Override
    public Integer selectUserBasketCount(String userId) {
        List<Object> objs = basketMapper.selectObjs(new QueryWrapper<Basket>()
                .select("ifnull(SUM(basket_count),0)")
                .eq("user_id", userId)
        );
        Object count = objs.get(0);
        return Integer.parseInt(count.toString());
    }


    /**
     * 最终购物车商品条目对象中的数据来自于2张表：购物车表，商品sku表
     *
     * 1.根据用户id查询购物车列表集合
     * 2.获取商品信息
     * 3.组装数据
     *
     *
     * @param userId
     * @return
     */
    @Override
    public CartVo selectUserBasketInfo(String userId) {
        CartVo cartVo = new CartVo();

        //根据用户标识查询购物车列表
        List<Basket> basketList = basketMapper.selectList(new LambdaQueryWrapper<Basket>()
                .eq(Basket::getUserId, userId)
        );
        //判断购物车是否有值
        if (CollectionUtil.isEmpty(basketList) || basketList.size() == 0) {
            return cartVo;
        }

        //从购物车集合中获取商品skuId集合
        List<Long> skuIdList = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
        //远程调用：根据商品skuId集合查询商品sku对象集合
        List<Sku> skuList = basketSkuFeign.getSkuListBySkuIds(skuIdList);
        if (CollectionUtil.isEmpty(skuList) || skuList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }

        //创建店铺集合对象
        List<ShopCart> shopCartList = new ArrayList<>();

        //循环购物车列表
        basketList.forEach(basket -> {
            //获取店铺id
            Long shopId = basket.getShopId();
            Long basketId = basket.getBasketId();
            Integer basketCount = basket.getBasketCount();
            //当前购物车中商品店铺id是否存在于之前的店铺集合中
            List<ShopCart> oneShopCart = shopCartList.stream().filter(shopCart -> shopCart.getShopId().equals(shopId)).collect(Collectors.toList());
            //判断当前集合是否有值
            ShopCart shopCart = null;
            List<CartItem> cartItemList = null;
            if (CollectionUtil.isEmpty(oneShopCart) || oneShopCart.size() == 0) {
                //创建店铺对象
                shopCart = new ShopCart();
                shopCart.setShopId(shopId);
                //创建店铺商品条目对象集合
                cartItemList = new ArrayList<>();

                //创建商品条目对象
                CartItem cartItem = new CartItem();
                cartItem.setBasketId(basketId);
                cartItem.setBasketCount(basketCount);
                //从skuList集合中过滤出与当前购物车中商品skuId一致的商品sku对象
                Sku sku1 = skuList.stream()
                        .filter(sku -> sku.getSkuId().equals(basket.getSkuId()))
                        .collect(Collectors.toList()).get(0);
                //将sku1对象中的属性copy到商品条目对象中
                BeanUtils.copyProperties(sku1, cartItem);
                cartItemList.add(cartItem);


                shopCart.setShopCartItems(cartItemList);
                shopCartList.add(shopCart);
            } else {
                //获取之前店铺对象
                shopCart = oneShopCart.get(0);
                //获取之前店铺商品条目对象集合
                cartItemList = shopCart.getShopCartItems();

                //创建商品条目对象
                CartItem cartItem = new CartItem();
                cartItem.setBasketId(basketId);
                cartItem.setBasketCount(basketCount);
                //从skuList集合中过滤出与当前购物车中商品skuId一致的商品sku对象
                Sku sku1 = skuList.stream()
                        .filter(sku -> sku.getSkuId().equals(basket.getSkuId()))
                        .collect(Collectors.toList()).get(0);
                //将sku1对象中的属性copy到商品条目对象中
                BeanUtils.copyProperties(sku1, cartItem);
                cartItemList.add(cartItem);
            }



        });

        cartVo.setShopCarts(shopCartList);
        return cartVo;
    }

    @Override
    public CartTotalAmount calculateUserCartTotalAmount(List<Long> basketIdList) {
        CartTotalAmount cartTotalAmount = new CartTotalAmount();
        //根据购物车id集合查询购物车对象集合
        List<Basket> basketList = basketMapper.selectBatchIds(basketIdList);
        if (CollectionUtil.isEmpty(basketList) || basketList.size() == 0) {
            return cartTotalAmount;
        }

        //从购物车对象集合中获取商品skuId集合
        List<Long> skuIdList = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
        //远程调用，根据商品skuId集合查询商品sku对象集合
        List<Sku> skuList = basketSkuFeign.getSkuListBySkuIds(skuIdList);
        if (CollectionUtil.isEmpty(skuList) || skuList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }
        List<BigDecimal> allOneSkuTotalAmount = new ArrayList<>();
        //循环购物车对象集合
        basketList.forEach(basket -> {
            //从商品sku对象集合中过滤出与当前购物车中商品sku对象一致的商品sku
            Sku sku1 = skuList.stream()
                    .filter(sku -> sku.getSkuId().equals(basket.getSkuId()))
                    .collect(Collectors.toList()).get(0);
            //计算单个商品总金额
            Integer basketCount = basket.getBasketCount();
            BigDecimal price = sku1.getPrice();
            BigDecimal oneSkuTotalAmount = price.multiply(new BigDecimal(basketCount));
            allOneSkuTotalAmount.add(oneSkuTotalAmount);
        });

        //计算所有商品总金额
        BigDecimal allSkuTotalAmount = allOneSkuTotalAmount.stream().reduce(BigDecimal::add).get();
        cartTotalAmount.setFinalMoney(allSkuTotalAmount);
        cartTotalAmount.setTotalMoney(allSkuTotalAmount);
        //计算运费：商品总额超过99免运费，否则运费6块
        if (allSkuTotalAmount.compareTo(new BigDecimal(99)) == -1) {
            cartTotalAmount.setTransMoney(new BigDecimal(6));
            cartTotalAmount.setFinalMoney(allSkuTotalAmount.add(new BigDecimal(6)));
        }

        return cartTotalAmount;
    }

    /**
     * 1.根据商品skuid和用户标识查询购物车中商品是否存在
     * 2.存在：更新数量
     * 3.不存在：添加商品到购物车
     *
     *
     *
     * @param basket
     */
    @Override
    public void changeItem(Basket basket) {
        //根据商品skuId和用户标识查询购物车记录
        Basket oldBasket = basketMapper.selectOne(new LambdaQueryWrapper<Basket>()
                .eq(Basket::getSkuId, basket.getSkuId())
                .eq(Basket::getUserId, basket.getUserId())
        );
        //判断是否存在
        if (ObjectUtil.isNull(oldBasket)) {
            //将商品添加到购物车中
            basket.setBasketDate(new Date());
            basket.setShopId(1L);
            basketMapper.insert(basket);
            return;
        }
        //存在：更新商品中购物车中的数量即可
        //计算商品的数量
        int finalCount = oldBasket.getBasketCount() + basket.getBasketCount();
        oldBasket.setBasketCount(finalCount);
        oldBasket.setBasketDate(new Date());
        basketMapper.updateById(oldBasket);
    }
}
