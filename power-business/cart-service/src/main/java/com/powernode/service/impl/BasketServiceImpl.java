package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.powernode.vo.CartVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
}
