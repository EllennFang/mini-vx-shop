package com.powernode.controller;

import com.powernode.service.BasketService;
import com.powernode.utils.AuthUtil;
import com.powernode.vo.CartVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "购物车接口管理")
@RequestMapping("p/shopCart")
@RestController
public class BasketController {

    @Autowired
    private BasketService basketService;


    @ApiOperation("查询用户购物车中商品数量")
    @GetMapping("prodCount")
    public ResponseEntity<Integer> loadUserBasketCount() {
        String userId = AuthUtil.getLoginUserId();
        Integer basketCount = basketService.selectUserBasketCount(userId);
        return ResponseEntity.ok(basketCount);
    }

//    p/shopCart/info
    @ApiOperation("查询用户购物车详情")
    @GetMapping("info")
    public ResponseEntity<CartVo> loadUserCartVoInfo() {
        String userId = AuthUtil.getLoginUserId();
        CartVo cartVo = basketService.selectUserBasketInfo(userId);
        return ResponseEntity.ok(cartVo);
    }
}
