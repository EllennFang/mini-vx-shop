package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.anno.Log;
import com.powernode.domain.SysRole;
import com.powernode.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

//    sys/role/page
    @ApiOperation("多条件分页查询角色列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('sys:role:page')")
    public ResponseEntity<Page<SysRole>> loadSysRolePage(Page<SysRole> page,SysRole sysRole) {
        page = sysRoleService.selectSysRolePage(page,sysRole);
        return ResponseEntity.ok(page);
    }

    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('sys:role:save')")
    @Log(operation = "新增角色")
    public ResponseEntity<Void> saveSysRole(@RequestBody SysRole sysRole) {
        sysRoleService.save(sysRole);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("修改角色")
    @PutMapping
    @PreAuthorize("hasAuthority('sys:role:update')")
    @Log(operation = "修改角色")
    public ResponseEntity<Void> updateSysRole(@RequestBody SysRole sysRole) {
        sysRoleService.updateById(sysRole);
        return ResponseEntity.ok().build();
    }


//    sys/role/info/3
    @ApiOperation("根据标识查询角色信息")
    @GetMapping("info/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:info')")
    public ResponseEntity<SysRole> loadSysRole(@PathVariable Long roleId) {
        SysRole sysRole = sysRoleService.getById(roleId);
        return ResponseEntity.ok(sysRole);
    }

    @ApiOperation("批量删除角色")
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @Log(operation = "批量删除角色")
    public ResponseEntity<Void> deleteSysRoleList(@RequestBody List<Long> roleIdList) {
        sysRoleService.removeByIds(roleIdList);
        return ResponseEntity.ok().build();
    }
}
