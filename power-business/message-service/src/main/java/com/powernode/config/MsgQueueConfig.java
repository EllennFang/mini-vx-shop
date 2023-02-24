package com.powernode.config;

import com.powernode.constant.QueueConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MsgQueueConfig {

    @Bean
    public Queue phoneCodeQueue() {
        return new Queue(QueueConstant.PHONE_CODE_QUEUE);
    }
}
