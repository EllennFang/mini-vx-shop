package com.powernode.feign.hystrix;

import com.powernode.domain.Prod;
import com.powernode.feign.SearchProdFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SearchProdFeignHystrix implements SearchProdFeign {

    @Override
    public List<Prod> getProdListByIds(List<Long> ids) {
        log.error("远程调用，根据商品id集合查询商品集合失败");
        return null;
    }
}
