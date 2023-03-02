package com.powernode.controller;

import com.powernode.domain.Basket;
import com.powernode.service.BasketService;
import com.powernode.utils.AuthUtil;
import com.powernode.vo.CartTotalAmount;
import com.powernode.vo.CartVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    p/shopCart/totalPay
    @ApiOperation("计算购物车中商品总金额")
    @PostMapping("totalPay")
    public ResponseEntity<CartTotalAmount> loadUserCartTotalAmount(@RequestBody List<Long> basketIdList) {
        CartTotalAmount cartTotalAmount = basketService.calculateUserCartTotalAmount(basketIdList);
        return ResponseEntity.ok(cartTotalAmount);
    }

//    p/shopCart/changeItem
    @ApiOperation("添加商品到购物车或修改购物车中商品数量")
    @PostMapping("changeItem")
    public ResponseEntity<Void> changeItem(@RequestBody Basket basket) {
        String userId = AuthUtil.getLoginUserId();
        basket.setUserId(userId);
        basketService.changeItem(basket);
        return ResponseEntity.ok().build();
    }

//    p/shopCart/deleteItem
    @ApiOperation("删除购物车中商品")
    @DeleteMapping("deleteItem")
    public ResponseEntity<Void> deleteItem(@RequestBody List<Long> basketIds) {
        basketService.removeByIds(basketIds);
        return ResponseEntity.ok().build();
    }

    //////////////////////远程接口调用/////////////////////////
    @GetMapping("getBasketsByIds")
    List<Basket> getBasketsByIds(@RequestParam List<Long> basketIds) {
        return basketService.listByIds(basketIds);
    }
}
