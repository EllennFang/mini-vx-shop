package com.powernode.feign.hystrix;


import com.powernode.domain.Basket;
import com.powernode.feign.OrderBasketFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderBasketFeignHystrix implements OrderBasketFeign {

    @Override
    public List<Basket> getBasketsByIds(List<Long> basketIds) {
        log.error("远程调用失败：根据购物车id集合查询购物车对象集合");
        return null;
    }
}
