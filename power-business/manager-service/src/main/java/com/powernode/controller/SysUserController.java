package com.powernode.controller;

import com.powernode.domain.SysUser;
import com.powernode.service.SysUserService;
import com.powernode.utils.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "后台管理接口管理")
@RequestMapping("sys/user")
@RestController
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("查询登录用户信息")
    @GetMapping("info")
    public ResponseEntity<SysUser> loadUserInfo() {
        //获取登录用户id
        String userId = AuthUtil.getLoginUserId();
        //根据用户标识获取用户信息
        SysUser sysUser = sysUserService.getById(userId);

        return ResponseEntity.ok(sysUser);
    }
}