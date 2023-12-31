package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdComm;
import com.powernode.service.ProdCommService;
import com.powernode.vo.ProdCommOverview;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "评论接口管理")
@RequestMapping("prod/prodComm")
@RestController
public class ProdCommController {

    @Autowired
    private ProdCommService prodCommService;

    @ApiOperation("多条件分页查询商品评论列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('prod:prodComm:page')")
    public ResponseEntity<Page<ProdComm>> loadProdCommPage(Page<ProdComm> page,ProdComm prodComm) {
        page = prodCommService.selectProdCommPage(page,prodComm);
        return ResponseEntity.ok(page);
    }

//    prod/prodComm/1
    @ApiOperation("根据标识查询评论详情")
    @GetMapping("{commId}")
    @PreAuthorize("hasAuthority('prod:prodComm:info')")
    public ResponseEntity<ProdComm> loadProdCommInfo(@PathVariable Long commId) {
        ProdComm prodComm = prodCommService.getById(commId);
        return ResponseEntity.ok(prodComm);
    }

    @ApiOperation("回复并审核评论")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:prodComm:update')")
    public ResponseEntity<Void> updateProdComm(@RequestBody ProdComm prodComm) {
        prodCommService.updateById(prodComm);
        return ResponseEntity.ok().build();
    }

    ////////////////////微信小程序数据接口/////////////////////////////////////
//    prod/prodComm/prodComm/prodCommData?prodId=95
    @ApiOperation("查询单个商品评论总览信息")
    @GetMapping("prodComm/prodCommData")
    public ResponseEntity<ProdCommOverview> loadProdCommOverview(@RequestParam Long prodId) {
        ProdCommOverview prodCommOverview = prodCommService.selectProdCommOverview(prodId);
        return ResponseEntity.ok(prodCommOverview);
    }

//    /prod/prodComm/prodComm/prodCommPageByProd?prodId=95&size=10&current=1&evaluate=2
    @ApiOperation("分页查询单个商品的评论")
    @GetMapping("prodComm/prodCommPageByProd")
    public ResponseEntity<Page<ProdComm>> loadProdCommPageByProd(Page<ProdComm> page,Long prodId,Long evaluate) {
        page = prodCommService.selectProdCommPageByProd(page,prodId,evaluate);
        return ResponseEntity.ok(page);
    }
}
