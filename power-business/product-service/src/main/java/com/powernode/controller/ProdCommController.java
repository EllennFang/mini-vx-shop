package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdComm;
import com.powernode.service.ProdCommService;
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
}
