package com.powernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Prod;
import com.powernode.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "搜索接口管理")
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @ApiOperation("根据分组标签查询商品集合")
    @GetMapping("prod/prodListByTagId")
    public ResponseEntity<Page<Prod>> loadProdListByTagId(Page<Prod> page, Long tagId) {
        page = searchService.selectProdListByTagId(page,tagId);
        return ResponseEntity.ok(page);
    }



}
