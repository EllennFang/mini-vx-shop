package com.powernode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * ClassName:ConfigServerApplication
 * Package:com.powernode
 * Description:
 * Date:2022/10/25 10:37
 * author:abc
 */
@SpringBootApplication
@EnableEurekaClient
@EnableConfigServer//开启远程配置中心服务
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class,args);
    }
}
