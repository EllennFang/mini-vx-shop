package com.powernode.controller;

import com.powernode.constant.GatewayConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "退出接口管理")
@RestController
public class LogoutController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("用户退出")
    @PostMapping("sys/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        //从请求头中获取jwt(bearer token)
        String jwt = request.getHeader(GatewayConstant.AUTHORIZATION);
        //获取token值
        String token = jwt.replaceFirst(GatewayConstant.BEARER,"");
        //将redis中的token删除
        stringRedisTemplate.delete(GatewayConstant.TOKEN_PREFIX+token);
        return ResponseEntity.ok().build();
    }
}
