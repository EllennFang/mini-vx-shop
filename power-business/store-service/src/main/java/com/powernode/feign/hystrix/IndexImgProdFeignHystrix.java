package com.powernode.feign.hystrix;

import com.powernode.domain.Prod;
import com.powernode.feign.IndexImgProdFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IndexImgProdFeignHystrix implements IndexImgProdFeign {

    @Override
    public Prod getProdById(Long prodId) {
        log.error("远程服务调用：根据商品id查询商品详情失败");
        return null;
    }
}
