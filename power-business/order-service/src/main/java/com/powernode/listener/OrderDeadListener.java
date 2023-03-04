package com.powernode.listener;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.constant.QueueConstant;
import com.powernode.domain.Order;
import com.powernode.model.ChangeStock;
import com.powernode.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OrderDeadListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = QueueConstant.ORDER_DEAD_QUEUE)
    public void handlerOrderDeadMsg(Message message, Channel channel) {
        //获取消息
        JSONObject jsonObject = JSONObject.parseObject(new String(message.getBody()));
        //获取订单编号
        String orderNumber = jsonObject.getString("orderNumber");
        //获取修改商品库存数量对象
        ChangeStock changeStock = jsonObject.getObject("changeStock", ChangeStock.class);

        //查询订单详情
        Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNumber, orderNumber)
        );
        //判断订单是否存在
        if (ObjectUtil.isNull(order)) {
            log.error("订单号码无效{}",orderNumber);
            try {
                //签收消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //判断订单是否已支付
        if (order.getIsPayed().equals(1)) {
            //已支付：签收消息
            try {
                //签收消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }


        try {
            //未支付：需要调用第三方支付平台的订单查询接口
            //从第三方平台获取订单详情，然后再来判断是否已支付
                //第三方通知：已支付->修改订单的状态
                //第三方通过：未支付->超时未支付(修改订单状态及商品数量回滚)
            orderService.orderRollBack(order,changeStock);
            //签收消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
