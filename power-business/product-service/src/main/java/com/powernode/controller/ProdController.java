package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Prod;
import com.powernode.service.ProdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
