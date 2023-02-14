package com.powernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.domain.Category;
import com.powernode.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("新增商品类目")
    @PostMapping
    @PreAuthorize("hasAuthority('prod:category:save')")
    public ResponseEntity<Void> saveCategory(@RequestBody Category category) {
        categoryService.save(category);
        return ResponseEntity.ok().build();
    }

//    prod/category/info/99
    @ApiOperation("根据标识查询类目详情")
    @GetMapping("info/{categoryId}")
    @PreAuthorize("hasAuthority('prod:category:info')")
    public ResponseEntity<Category> loadCategoryInfo(@PathVariable Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return ResponseEntity.ok(category);
    }

    @ApiOperation("修改商品类目信息")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:category:update')")
    public ResponseEntity<Void> updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return ResponseEntity.ok().build();
    }

//    prod/category/93
    @ApiOperation("删除商品类目")
    @DeleteMapping("{categoryId}")
    @PreAuthorize("hasAuthority('prod:category:delete')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.removeById(categoryId);
        return ResponseEntity.ok().build();
    }
}
