package com.powernode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * ClassName:ManagerServiceApplication
 * Package:com.powernode
 * Description:
 * Date:2022/10/27 11:25
 * author:abc
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCaching//开启注解式缓存（默认使用的缓存中间件是redis）
public class ManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagerServiceApplication.class,args);
    }
}
