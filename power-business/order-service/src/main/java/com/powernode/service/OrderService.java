package com.powernode.service;

import com.powernode.domain.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.dto.OrderConfirmDto;
import com.powernode.model.ChangeStock;
import com.powernode.vo.OrderStatus;
import com.powernode.vo.OrderVo;

public interface OrderService extends IService<Order>{


    /**
     * 查询用户订单总览信息
     * @param userId
     * @return
     */
    OrderStatus selectUserOrderStatus(String userId);

    /**
     * 订单确认
     * @param userId
     * @param orderConfirmDto
     * @return
     */
    OrderVo selectOrderConfirmInfo(String userId, OrderConfirmDto orderConfirmDto);

    /**
     * 提交订单
     * @param userId
     * @param orderVo
     * @return
     */
    String submitOrder(String userId, OrderVo orderVo);

    /**
     * 修改订单状态及数据的回滚
     * @param order
     * @param changeStock
     */
    void orderRollBack(Order order, ChangeStock changeStock);

}
