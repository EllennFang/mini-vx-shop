package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Prod;
import com.powernode.service.ProdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口管理")
@RequestMapping("prod/prod")
@RestController
public class ProdController {

    @Autowired
    private ProdService prodService;

    @ApiOperation("多条件分页查询商品列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('prod:prod:page')")
    public ResponseEntity<Page<Prod>> loadProdPage(Page<Prod> page,Prod prod) {
        page = prodService.selectProdPage(page,prod);
        return ResponseEntity.ok(page);
    }

    @ApiOperation("新增商品")
    @PostMapping
    @PreAuthorize("hasAuthority('prod:prod:save')")
    public ResponseEntity<Void> saveProd(@RequestBody Prod prod) {
        prodService.save(prod);
        return ResponseEntity.ok().build();
    }

//    prod/prod/info/99
    @ApiOperation("根据标识查询商品详情")
    @GetMapping("info/{prodId}")
    @PreAuthorize("hasAuthority('prod:prod:info')")
    public ResponseEntity<Prod> loadProdInfo(@PathVariable Long prodId) {
        Prod prod = prodService.getById(prodId);
        return ResponseEntity.ok(prod);
    }

    @ApiOperation("修改商品信息")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:prod:update')")
    public ResponseEntity<Void> updateProd(@RequestBody Prod prod) {
        prodService.updateById(prod);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("批量删除商品")
    @DeleteMapping
    @PreAuthorize("hasAuthority('prod:prod:delete')")
    public ResponseEntity<Void> deleteProd(@RequestBody List<Long> prodIds) {
        prodService.removeByIds(prodIds);
        return ResponseEntity.ok().build();
    }

    ////////////////////远程接口调用/////////////////////////////////
    @GetMapping("getProdById")
    Prod getProdById(@RequestParam Long prodId) {
        return prodService.getById(prodId);
    }

    @GetMapping("getProdListByIds")
    List<Prod> getProdListByIds(@RequestParam List<Long> ids) {
        return prodService.listByIds(ids);
    }


    ////////////////////微信小程序数据接口/////////////////////////////
//    prod/prod/prod/prodInfo?prodId=60
    @ApiOperation("根据标识查询商品详情和商品sku")
    @GetMapping("prod/prodInfo")
    public ResponseEntity<Prod> loadProdAndSkuDetail(@RequestParam Long prodId) {
        Prod prod = prodService.selectProdAndSkuDetailById(prodId);
        return ResponseEntity.ok(prod);
    }
}
