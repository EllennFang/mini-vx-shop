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

//    p/address/addrInfo/10
    @ApiOperation("根据标识查询地址详情")
    @GetMapping("addrInfo/{addrId}")
    public ResponseEntity<UserAddr> loadUserAddrInfo(@PathVariable Long addrId) {
        UserAddr userAddr = userAddrService.getById(addrId);
        return ResponseEntity.ok(userAddr);
    }

//    p/address/updateAddr
    @ApiOperation("修改用户收货地址信息")
    @PutMapping("updateAddr")
    public ResponseEntity<Void> updateUserAddr(@RequestBody UserAddr userAddr) {
        String userId = AuthUtil.getLoginUserId();
        userAddr.setUserId(userId);
        userAddrService.updateById(userAddr);
        return ResponseEntity.ok().build();
    }

//    p/address/deleteAddr/9
    @ApiOperation("删除用户收货地址")
    @DeleteMapping("deleteAddr/{addrId}")
    public ResponseEntity<Void> deleteUserAddr(@PathVariable Long addrId) {
        String userId = AuthUtil.getLoginUserId();
        userAddrService.deleteUserAddr(userId,addrId);
        return ResponseEntity.ok().build();
    }

//    p/address/defaultAddr/13
    @ApiOperation("设置用户默认收货地址")
    @PutMapping("defaultAddr/{addrId}")
    public ResponseEntity<Void> updateUserDefaultAddr(@PathVariable Long addrId) {
        String userId = AuthUtil.getLoginUserId();
        userAddrService.updateUserDefaultAddr(userId,addrId);
        return ResponseEntity.ok().build();
    }
}
