package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdComm;
import com.powernode.service.ProdCommService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
