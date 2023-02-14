package com.powernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.domain.Category;
import com.powernode.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "商品类型接口管理")
@RequestMapping("prod/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("查询商品类目集合")
    @GetMapping("table")
    @PreAuthorize("hasAuthority('prod:category:page')")
    public ResponseEntity<List<Category>> loadCategoryList() {
        List<Category> list = categoryService.list();
        return ResponseEntity.ok(list);
    }

//    prod/category/listCategory
    @ApiOperation("查询商品一级类目集合")
    @GetMapping("listCategory")
    @PreAuthorize("hasAuthority('prod:category:page')")
    public ResponseEntity<List<Category>> loadRootCategoryList() {
        List<Category> root = categoryService.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, 0)
        );
        return ResponseEntity.ok(root);
    }
}
