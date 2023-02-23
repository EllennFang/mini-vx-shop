package com.powernode.controller;

import com.powernode.domain.UserAddr;
import com.powernode.service.UserAddrService;
import com.powernode.utils.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "收货地址接口管理")
@RequestMapping("p/address")
@RestController
public class UserAddrController {

    @Autowired
    private UserAddrService userAddrService;


    @ApiOperation("查询用户收货地址列表")
    @GetMapping("list")
    public ResponseEntity<List<UserAddr>> loadUserAddrList() {
        String userId = AuthUtil.getLoginUserId();
        List<UserAddr> userAddrList = userAddrService.selectUserAddrList(userId);
        return ResponseEntity.ok(userAddrList);
    }

//    p/address/addAddr
    @ApiOperation("新增收货地址")
    @PostMapping("addAddr")
    public ResponseEntity<Void> saveUserAddr(@RequestBody UserAddr userAddr) {
        String userId = AuthUtil.getLoginUserId();
        userAddr.setUserId(userId);
        userAddrService.save(userAddr);
        return ResponseEntity.ok().build();
    }
}
