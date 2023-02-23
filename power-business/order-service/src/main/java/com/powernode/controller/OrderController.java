package com.powernode.controller;

import com.powernode.service.OrderService;
import com.powernode.utils.AuthUtil;
import com.powernode.vo.OrderStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "订单接口管理")
@RequestMapping("p/myOrder")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("查询用户订单总览信息")
    @GetMapping("orderCount")
    public ResponseEntity<OrderStatus> loadUserOrderStatus() {
        String userId = AuthUtil.getLoginUserId();
        OrderStatus orderStatus = orderService.selectUserOrderStatus(userId);
        return ResponseEntity.ok(orderStatus);
    }
}
