package com.powernode.controller;

import com.powernode.dto.OrderConfirmDto;
import com.powernode.service.OrderService;
import com.powernode.utils.AuthUtil;
import com.powernode.vo.OrderStatus;
import com.powernode.vo.OrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    p/myOrder/confirm
    @ApiOperation("订单确认")
    @PostMapping("confirm")
    public ResponseEntity<OrderVo> loadOrderConfirmInfo(@RequestBody OrderConfirmDto orderConfirmDto) {
        String userId = AuthUtil.getLoginUserId();
        OrderVo orderVo = orderService.selectOrderConfirmInfo(userId,orderConfirmDto);
        return ResponseEntity.ok(orderVo);
    }

//    p/myOrder/submit
    @ApiOperation("提交订单")
    @PostMapping("submit")
    public ResponseEntity<String> submitOrder(@RequestBody OrderVo orderVo) {
        String userId = AuthUtil.getLoginUserId();
        String orderNumber = orderService.submitOrder(userId,orderVo);
        return ResponseEntity.ok(orderNumber);
    }
}
