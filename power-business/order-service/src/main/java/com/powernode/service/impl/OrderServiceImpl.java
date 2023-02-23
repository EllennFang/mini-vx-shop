package com.powernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.Order;
import com.powernode.mapper.OrderMapper;
import com.powernode.service.OrderService;
import com.powernode.vo.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{


    @Autowired
    private OrderMapper orderMapper;


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
}
