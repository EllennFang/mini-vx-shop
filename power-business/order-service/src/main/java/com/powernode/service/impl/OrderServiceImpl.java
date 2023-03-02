package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.*;
import com.powernode.dto.OrderConfirmDto;
import com.powernode.feign.OrderBasketFeign;
import com.powernode.feign.OrderSkuFeign;
import com.powernode.feign.OrderUserAddrFeign;
import com.powernode.mapper.OrderMapper;
import com.powernode.model.ShopOrder;
import com.powernode.service.OrderService;
import com.powernode.vo.OrderStatus;
import com.powernode.vo.OrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderUserAddrFeign orderUserAddrFeign;

    @Autowired
    private OrderSkuFeign orderSkuFeign;

    @Autowired
    private OrderBasketFeign orderBasketFeign;


    @Override
    public OrderStatus selectUserOrderStatus(String userId) {


        //查询待支付数量
        Integer unPay = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getDeleteStatus, 0)
                .eq(Order::getStatus, 1)
        );

        //查询待发货数量
        Integer payed = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getDeleteStatus, 0)
                .eq(Order::getStatus, 2)
        );

        //查询待收货数量
        Integer consignment = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getDeleteStatus, 0)
                .eq(Order::getStatus, 3)
        );


        return OrderStatus.builder().unPay(unPay).payed(payed).consignment(consignment).build();
    }

    @Override
    public OrderVo selectOrderConfirmInfo(String userId, OrderConfirmDto orderConfirmDto) {
        OrderVo orderVo = new OrderVo();
        //远程调用：查询用户默认收货地址
        UserAddr userDefaultAddr = orderUserAddrFeign.getUserDefaultAddr(userId);
        orderVo.setUserAddr(userDefaultAddr);

        //判断订单确认来自于哪儿（商品详情页面还是购物车页面）
        List<Long> basketIds = orderConfirmDto.getBasketIds();
        if (CollectionUtil.isEmpty(basketIds) || basketIds.size() == 0) {
            //订单来自于：商品详情页面
            productToConfirm(userId,orderVo,orderConfirmDto.getOrderItem());
        } else {
            //订单来自于：购物车页面
            orderVo.setSource(1);
            cartToConfirm(userId,orderVo,basketIds);
        }

        return orderVo;
    }

    private void cartToConfirm(String userId,OrderVo orderVo, List<Long> basketIds) {

        //远程调用:根据购物车id集合查询购物车对象集合
        List<Basket> basketList = orderBasketFeign.getBasketsByIds(basketIds);
        if (CollectionUtil.isEmpty(basketList) || basketList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }

        //从购物车集合对象中获取商品skuId集合
        List<Long> skuIdList = basketList.stream().map(Basket::getSkuId).collect(Collectors.toList());
        //远程调用：根据商品skuId集合查询商品sku对象集合
        List<Sku> skuList = orderSkuFeign.getSkuListBySkuIds(skuIdList);
        if (CollectionUtil.isEmpty(skuList) || skuList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }
        List<ShopOrder> shopOrderList = new ArrayList<>();
        //将购物车对象集合使用stream流程进行分组
        Map<Long, List<Basket>> allShopOrderMap = basketList.stream().collect(Collectors.groupingBy(Basket::getShopId));
        List<BigDecimal> allOneSkuTotalAmounts = new ArrayList<>();
        List<Integer> allOneSkuCounts = new ArrayList<>();
        //循环遍历map集合
        allShopOrderMap.forEach((shopId,baskets) -> {
            //创建一个店铺对象
            ShopOrder shopOrder = new ShopOrder();
            List<OrderItem> orderItemList = new ArrayList<>();
            //循环遍历当前店铺中的购物车记录
            for(Basket basket:baskets){
                //创建订单商品条目对象
                OrderItem orderItem = new OrderItem();
                //将购物軖对象中的属性copy到商品条目对象中
                BeanUtils.copyProperties(basket,orderItem);
                //从商品sku集合对象中过滤出与当前购物车记录中商品skuId一致的商品对象
                Sku sku1 = skuList.stream().filter(sku -> sku.getSkuId().equals(basket.getSkuId())).collect(Collectors.toList()).get(0);
                BeanUtils.copyProperties(sku1,orderItem);
                orderItem.setUserId(userId);
                orderItem.setRecTime(new Date());
                orderItem.setCommSts(0);
                //计算 单个商品总金额
                Integer basketCount = basket.getBasketCount();
                orderItem.setProdCount(basketCount);
                allOneSkuCounts.add(basketCount);
                BigDecimal oneSkuTotalAmount = sku1.getPrice().multiply(new BigDecimal(basketCount));
                allOneSkuTotalAmounts.add(oneSkuTotalAmount);
                orderItem.setProductTotalAmount(oneSkuTotalAmount);

                orderItemList.add(orderItem);
            }

            shopOrder.setShopCartItemDiscounts(orderItemList);
            shopOrderList.add(shopOrder);
        });

        //计算购买商品总数量
        Integer allSkuTotalCount = allOneSkuCounts.stream().reduce(Integer::sum).get();
        //计算所有商品总金额
        BigDecimal allSkuTotalAmount = allOneSkuTotalAmounts.stream().reduce(BigDecimal::add).get();
        orderVo.setTotalCount(allSkuTotalCount);
        orderVo.setTotal(allSkuTotalAmount);
        orderVo.setActualTotal(allSkuTotalAmount);
        //计算运费
        if (allSkuTotalAmount.compareTo(new BigDecimal(99)) == -1) {
            orderVo.setTransfee(new BigDecimal(6));
            orderVo.setActualTotal(allSkuTotalAmount.add(new BigDecimal(6)));
        }
        orderVo.setShopCartOrders(shopOrderList);
    }

    private void productToConfirm(String userId,OrderVo orderVo, OrderItem orderItem) {
        Integer prodCount = orderItem.getProdCount();

        //获取商品skuId
        Long skuId = orderItem.getSkuId();
        List<Sku> skuList = orderSkuFeign.getSkuListBySkuIds(Arrays.asList(skuId));
        if (CollectionUtil.isEmpty(skuList) || skuList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }
        Sku sku = skuList.get(0);
        BigDecimal price = sku.getPrice();

        orderVo.setTotalCount(prodCount);
        //计算单个商品总金额
        BigDecimal oneSkuTotalAmount = price.multiply(new BigDecimal(prodCount));
        orderVo.setTotal(oneSkuTotalAmount);
        orderVo.setActualTotal(oneSkuTotalAmount);
        //计算运费
        if (oneSkuTotalAmount.compareTo(new BigDecimal(99)) == -1) {
            orderVo.setTransfee(new BigDecimal(6));
            orderVo.setActualTotal(oneSkuTotalAmount.add(new BigDecimal(6)));
        }

        //创建店铺对象
        List<ShopOrder> shopOrderList = new ArrayList<>();
        ShopOrder shopOrder = new ShopOrder();
        List<OrderItem> orderItemList = new ArrayList<>();

        //将商品sku对象中的属性copy到商品条目对象中

        BeanUtils.copyProperties(sku,orderItem);
        orderItem.setUserId(userId);
        orderItem.setProductTotalAmount(oneSkuTotalAmount);
        orderItem.setRecTime(new Date());
        orderItem.setCommSts(0);

        orderItemList.add(orderItem);
        shopOrder.setShopCartItemDiscounts(orderItemList);
        shopOrderList.add(shopOrder);
        orderVo.setShopCartOrders(shopOrderList);
    }
}
