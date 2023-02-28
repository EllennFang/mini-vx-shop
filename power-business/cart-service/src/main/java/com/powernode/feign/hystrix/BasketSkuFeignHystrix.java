package com.powernode.feign.hystrix;

import com.powernode.domain.Sku;
import com.powernode.feign.BasketSkuFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BasketSkuFeignHystrix implements BasketSkuFeign {

    @Override
    public List<Sku> getSkuListBySkuIds(List<Long> skuIdList) {
        log.error("远程调用,根据商品skuId集合查询商品sku对象集合失败");
        return null;
    }
}
