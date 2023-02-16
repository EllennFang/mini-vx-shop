package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdProp;
import com.powernode.service.ProdPropService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "商品规则接口管理")
@RequestMapping("prod/spec")
@RestController
public class ProdSpecController {

    @Autowired
    private ProdPropService prodPropService;


    @ApiOperation("多条件分页查询商品规格列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('prod:spec:page')")
    public ResponseEntity<Page<ProdProp>> loadProdSpecPage(Page<ProdProp> page,ProdProp prodProp) {
        page = prodPropService.selectProdSpecPage(page,prodProp);
        return ResponseEntity.ok(page);
    }


    @ApiOperation("新增商品规格")
    @PostMapping
    @PreAuthorize("hasAuthority('prod:spec:save')")
    public ResponseEntity<Void> saveProdSpec(@RequestBody ProdProp prodProp) {
        prodPropService.save(prodProp);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("修改商品规格")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:spec:update')")
    public ResponseEntity<Void> updateProdSpec(@RequestBody ProdProp prodProp) {
        prodPropService.updateById(prodProp);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("删除商品规格")
    @DeleteMapping("{propId}")
    @PreAuthorize("hasAuthority('prod:spec:delete')")
    public ResponseEntity<Void> deleteProdSpec(@PathVariable Long propId) {
        prodPropService.removeById(propId);
        return ResponseEntity.ok().build();
    }
}
