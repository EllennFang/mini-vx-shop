package com.powernode.controller;

import com.powernode.domain.Area;
import com.powernode.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "地址接口管理")
@RequestMapping("admin/area")
@RestController
public class AreaController {

    @Autowired
    private AreaService areaService;


    @ApiOperation("查询地址列表")
    @GetMapping("list")
    @PreAuthorize("hasAuthority('admin:area:list')")
    public ResponseEntity<List<Area>> loadAreaList() {
        List<Area> list = areaService.list();
        return ResponseEntity.ok(list);
    }
}
