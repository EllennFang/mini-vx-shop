package com.powernode.controller;

import com.powernode.service.UserService;
import com.powernode.utils.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "短信接口管理")
@RequestMapping("p/sms")
@RestController
public class SendController {

    @Autowired
    private UserService userService;

//    p/sms/send
    @ApiOperation("获取短信验证码")
    @PostMapping("send")
    public ResponseEntity<String> sendPhoneCode(@RequestBody Map<String,Object> map) {
        String userId = AuthUtil.getLoginUserId();
        map.put("userId",userId);
        userService.send(map);
        return ResponseEntity.ok("发送成功");
    }
}
