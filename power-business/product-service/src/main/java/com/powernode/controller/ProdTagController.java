package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdTag;
import com.powernode.service.ProdTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "分组标签接口管理")
@RequestMapping("prod/prodTag")
@RestController
public class ProdTagController {

    @Autowired
    private ProdTagService prodTagService;

    @ApiOperation("多条件分页查询分组标签列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('prod:prodTag:page')")
    public ResponseEntity<Page<ProdTag>> loadProdTagPage(Page<ProdTag> page,ProdTag prodTag) {
        page = prodTagService.selectProdTagPage(page,prodTag);
        return ResponseEntity.ok(page);
    }
}
