package com.powernode.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powernode.constant.GatewayConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * JWT全局Token校验过滤
 */
@Component
public class JwtCheckFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 1.拿到路径
     * 2.判断需要放行的路径
     * 3.拿到请求头
     * 4.获取token
     * 5.判断token
     * 6.拦截等
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //拿到请求
        ServerHttpRequest request = exchange.getRequest();
        //获取请求路径
        String path = request.getURI().getPath();
        //判断当前请求路径是否需要放行通过
        if (GatewayConstant.ALLOW_URLS.contains(path)) {
            //放行
            return chain.filter(exchange);
        }
        //当前请求路径需要校验,即校验请求头中token值
        //获取请求头中的token值
        List<String> auths = request.getHeaders().get(GatewayConstant.AUTHORIZATION);
        //判断是否有token
        if (CollectionUtil.isNotEmpty(auths)) {
            //有：获取集合中的第1个
            String token = auths.get(0);//token值的格式：bearer jwt
            //判断token是否有值
            if (StrUtil.isNotBlank(token)) {
                //截取token值中的jwt
                String jwt = token.replaceFirst(GatewayConstant.BEARER, "");
                //判断jwt是否为空并且是否为我们系统颁发的jwt
                if (StrUtil.isNotBlank(jwt) && stringRedisTemplate.hasKey(GatewayConstant.TOKEN_PREFIX+jwt)) {
                    //放行
                    return chain.filter(exchange);
                }
            }
        }
        //拦截
        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("content-type", "application/json;charset=utf-8");
        //组装业务返回值
        HashMap<String,Object> map = new HashMap<>(4);
        map.put("code", HttpStatus.UNAUTHORIZED.value());
        map.put("msg","请求未授权");
        //创建转换器
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
