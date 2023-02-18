package com.powernode.feign;

import com.powernode.domain.Prod;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "product-service")
public interface IndexImgProdFeign {

    @GetMapping("prod/prod/getProdById")
    Prod getProdById(@RequestParam Long prodId);

}
