package com.powernode.feign.hystrix;


import com.powernode.domain.Sku;
import com.powernode.feign.OrderSkuFeign;
import com.powernode.model.ChangeStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderSkuFeignHystrix implements OrderSkuFeign {

    @Override
    public List<Sku> getSkuListBySkuIds(List<Long> skuIdList) {
        log.error("远程调用，根据商品skuId集合查询商品sku对象集合失败");
        return null;
    }

    @Override
    public void changeStock(ChangeStock changeStock) {
        log.error("远程调用失败，修改商品prod和sku库存数量");
    }
}
