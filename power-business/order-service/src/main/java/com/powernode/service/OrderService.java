package com.powernode.service;

import com.powernode.domain.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.vo.OrderStatus;

public interface OrderService extends IService<Order>{


    /**
     * 查询用户订单总览信息
     * @param userId
     * @return
     */
    OrderStatus selectUserOrderStatus(String userId);
}
