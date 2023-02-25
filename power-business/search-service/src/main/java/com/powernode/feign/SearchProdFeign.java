package com.powernode.feign;

import com.powernode.domain.Prod;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "product-service")
public interface SearchProdFeign {

    @GetMapping("prod/prod/getProdListByIds")
    List<Prod> getProdListByIds(@RequestParam List<Long> ids);

}
