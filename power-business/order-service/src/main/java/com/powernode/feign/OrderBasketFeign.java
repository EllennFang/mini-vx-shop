package com.powernode.feign;

import com.powernode.domain.Basket;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "cart-service")
public interface OrderBasketFeign {

    @GetMapping("p/shopCart/getBasketsByIds")
    List<Basket> getBasketsByIds(@RequestParam List<Long> basketIds);
}
