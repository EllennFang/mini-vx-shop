package com.powernode.controller;

import com.powernode.service.UserService;
import com.powernode.utils.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    p/sms/savePhone
    @ApiOperation("绑定用户手机号码")
    @PostMapping("savePhone")
    public ResponseEntity<Void> savePhone(@RequestBody Map<String,Object> map) {
        String userId = AuthUtil.getLoginUserId();
        map.put("userId",userId);
        userService.savePhone(map);
        return ResponseEntity.ok().build();
    }
}
