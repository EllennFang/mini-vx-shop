package com.powernode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdTag;
import com.powernode.service.ProdTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @ApiOperation("新增商品分组标签")
    @PostMapping
    @PreAuthorize("hasAuthority('prod:prodTag:save')")
    public ResponseEntity<Void> saveProdTag(@RequestBody ProdTag prodTag) {
        prodTagService.save(prodTag);
        return ResponseEntity.ok().build();
    }
//    prod/prodTag/info/6
    @ApiOperation("根据标识查询商品分组标签详情")
    @GetMapping("info/{tagId}")
    @PreAuthorize("hasAuthority('prod:prodTag:info')")
    public ResponseEntity<ProdTag> loadProdTagInfo(@PathVariable Long tagId) {
        ProdTag prodTag = prodTagService.getById(tagId);
        return ResponseEntity.ok(prodTag);
    }

    @ApiOperation("修改商品分组标签")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:prodTag:update')")
    public ResponseEntity<Void> updateProdTag(@RequestBody ProdTag prodTag) {
        prodTagService.updateById(prodTag);
        return ResponseEntity.ok().build();
    }

//    prod/prodTag/6
    @ApiOperation("删除商品分组标签")
    @DeleteMapping("{tagId}")
    @PreAuthorize("hasAuthority('prod:prodTag:delete')")
    public ResponseEntity<Void> deleteProdTag(@PathVariable Long tagId) {
        prodTagService.removeById(tagId);
        return ResponseEntity.ok().build();
    }

//    prod/prodTag/listTagList
    @ApiOperation("查询商品分组标签集合")
    @GetMapping("listTagList")
    @PreAuthorize("hasAuthority('prod:prodTag:page')")
    public ResponseEntity<List<ProdTag>> loadProdTagList() {
        List<ProdTag> list = prodTagService.list();
        return ResponseEntity.ok(list);
    }


    ////////////////////////微信小程序数据接口//////////////////
//    prod/prodTag/prodTagList
    @ApiOperation("查询商品分组标签集合")
    @GetMapping("prodTagList")
    public ResponseEntity<List<ProdTag>> loadFrontProdTagList() {
        List<ProdTag> list = prodTagService.selectFrontProdTagList();
        return ResponseEntity.ok(list);
    }
}
