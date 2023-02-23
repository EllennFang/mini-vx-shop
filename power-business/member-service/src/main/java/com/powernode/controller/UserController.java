package com.powernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.domain.User;
import com.powernode.service.UserService;
import com.powernode.utils.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Api(tags = "用户接口管理")
@RequestMapping("p/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @ApiOperation("查询用户是否绑定手机号码")
    @GetMapping("isBindPhone")
    public ResponseEntity<Boolean> isBindPhone() {
        String userId = AuthUtil.getLoginUserId();
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, userId)
        );
        return ResponseEntity.ok(StringUtils.hasText(user.getUserMobile()));
    }

//    p/user/setUserInfo
    @ApiOperation("更新用户头像和昵称")
    @PutMapping("setUserInfo")
    public ResponseEntity<Void> setUserInfo(@RequestBody User user) {
        String userId = AuthUtil.getLoginUserId();
        user.setUserId(userId);
        user.setModifyTime(new Date());
        userService.update(user,new LambdaQueryWrapper<User>()
                .eq(User::getUserId,userId)
        );
        return ResponseEntity.ok().build();
    }
}
