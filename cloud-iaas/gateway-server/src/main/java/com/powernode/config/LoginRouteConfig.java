package com.powernode.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.powernode.constant.GatewayConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 登录的路由配置
 * 作用：把oAuth2.0颁发的token存储到redis中
 */
@Configuration
public class LoginRouteConfig {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 代码方式的路由存储token
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator loginRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-server-route", r -> r.path("/oauth/token").filters(
                        f -> f.modifyResponseBody(String.class, String.class, (exchange, s) -> {
                            //s为响应的结果，类型为json，结构为{"access_token","expires_in"}
                            //将响应的json数据转换为json对象
                            JSONObject jsonObject = JSON.parseObject(s);
                            //查看是否包含access_token
                            if (jsonObject.containsKey("access_token")) {
                                //有：存放到redis中
                                //获取token值和过期时间
                                String access_token = jsonObject.getString("access_token");
                                Long expires_in = jsonObject.getLong("expires_in");
                                //将获取的值存放到redis中
                                stringRedisTemplate.opsForValue().set(GatewayConstant.TOKEN_PREFIX+access_token,"", Duration.ofSeconds(expires_in));
                            }
                            return Mono.just(s);
                            //uri是路由的目的地,(lb://auth-server是授权中心服务名称)
                        })).uri("lb://auth-server"))
                .build();
    }
}
