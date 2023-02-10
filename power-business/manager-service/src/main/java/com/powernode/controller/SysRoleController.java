package com.powernode.controller;

import com.powernode.domain.SysRole;
import com.powernode.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "角色接口管理")
@RequestMapping("sys/role")
@RestController
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;


    @ApiOperation("查询系统角色列表")
    @GetMapping("list")
    @PreAuthorize("hasAuthority('sys:role:page')")
    public ResponseEntity<List<SysRole>> loadSysRoleList() {
        List<SysRole> list = sysRoleService.list();
        return ResponseEntity.ok(list);
    }
}
