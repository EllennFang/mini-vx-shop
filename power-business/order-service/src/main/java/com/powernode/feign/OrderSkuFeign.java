package com.powernode.feign;

import com.powernode.domain.Sku;
import com.powernode.model.ChangeStock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "product-service")
public interface OrderSkuFeign {

    @GetMapping("prod/prod/getSkuListBySkuIds")
    List<Sku> getSkuListBySkuIds(@RequestParam List<Long> skuIdList);

    @PostMapping("prod/prod/changeStock")
    void changeStock(@RequestBody ChangeStock changeStock);
}
