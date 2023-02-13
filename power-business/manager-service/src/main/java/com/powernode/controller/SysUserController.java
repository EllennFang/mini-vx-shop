package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.anno.Log;
import com.powernode.domain.SysUser;
import com.powernode.service.SysUserService;
import com.powernode.utils.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    sys/user/page
    @ApiOperation("多条件分页查询管理员列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('sys:user:page')")
    public ResponseEntity<Page<SysUser>> loadSysUserPage(Page<SysUser> page,SysUser sysUser) {
        page = sysUserService.selectSysUserPage(page,sysUser);
        return ResponseEntity.ok(page);
    }

    @ApiOperation("新增管理员")
    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:save')")
    @Log(operation = "新增管理员")
    public ResponseEntity<Void> saveSysUser(@RequestBody SysUser sysUser) {
        sysUserService.save(sysUser);
        return ResponseEntity.ok().build();
    }

//    sys/user/info/8
    @ApiOperation("查询管理员详情")
    @GetMapping("info/{id}")
    @PreAuthorize("hasAuthority('sys:user:info')")
    public ResponseEntity<SysUser> loadSysUserInfo(@PathVariable Long id) {
        SysUser sysUser = sysUserService.getById(id);
        return ResponseEntity.ok(sysUser);
    }

    @ApiOperation("修改管理员信息")
    @PutMapping
    @PreAuthorize("hasAuthority('sys:user:update')")
    @Log(operation = "修改管理员信息")
    public ResponseEntity<Void> updateSysUser(@RequestBody SysUser sysUser) {
        sysUserService.updateById(sysUser);
        return ResponseEntity.ok().build();
    }

//    sys/user/8,7
    @ApiOperation("批量删除管理员")
    @DeleteMapping("{ids}")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @Log(operation = "批量删除管理员")
    public ResponseEntity<Void> deleteSysUser(@PathVariable List<Long> ids) {
        sysUserService.removeByIds(ids);
        return ResponseEntity.ok().build();
    }
}
