package com.powernode.config;

import com.powernode.constant.QueueConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OrderQueueConfig {

    /**
     * 创建延迟队列
     * @return
     */
    @Bean
    public Queue orderMsQueue() {

        Map<String,Object> map = new HashMap<>();
        //配置消息存活的时候
        map.put("x-message-ttl",60*1000);
        //配置消息死后去哪个交换机
        map.put("x-dead-letter-exchange",QueueConstant.ORDER_DEAD_EX);
        //配置消息死后去哪个路由key
        map.put("x-dead-letter-routing-key",QueueConstant.ORDER_DEAD_KEY);

        return new Queue(QueueConstant.ORDER_MS_QUEUE,true,false,false,map);
    }


    @Bean
    public Queue orderDeadQueue() {
        return new Queue(QueueConstant.ORDER_DEAD_QUEUE);
    }

    @Bean
    public DirectExchange orderDeadEx() {
        return new DirectExchange(QueueConstant.ORDER_DEAD_EX);
    }

    @Bean
    public Binding orderDeadBind() {
        return BindingBuilder.bind(orderDeadQueue()).to(orderDeadEx()).with(QueueConstant.ORDER_DEAD_KEY);
    }
}
