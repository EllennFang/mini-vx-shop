package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.Order;
import com.powernode.domain.OrderItem;
import com.powernode.domain.Sku;
import com.powernode.domain.UserAddr;
import com.powernode.dto.OrderConfirmDto;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderUserAddrFeign orderUserAddrFeign;

    @Autowired
    private OrderSkuFeign orderSkuFeign;


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
//            cartToConfirm(orderVo,basketIds);
        }

        return orderVo;
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
