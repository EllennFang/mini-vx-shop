package com.powernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.domain.UserCollection;
import com.powernode.service.UserCollectionService;
import com.powernode.utils.AuthUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户收藏接口管理")
@RequestMapping("p/collection")
@RestController
public class UserCollectionController {

    @Autowired
    private UserCollectionService userCollectionService;

    @ApiOperation("查询用户收藏商品的数量")
    @GetMapping("count")
    public ResponseEntity<Integer> loadUserCollectionCount() {
        String userId = AuthUtil.getLoginUserId();
        int count = userCollectionService.count(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
        );
        return ResponseEntity.ok(count);
    }

//    p/collection/isCollection?prodId=95
    @ApiOperation("用户是否收藏当前商品")
    @GetMapping("isCollection")
    public ResponseEntity<Boolean> isCollection(@RequestParam Long prodId) {
        String userId = AuthUtil.getLoginUserId();
        int count = userCollectionService.count(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getProdId, prodId)
        );
        return ResponseEntity.ok(count == 1);
    }
}
