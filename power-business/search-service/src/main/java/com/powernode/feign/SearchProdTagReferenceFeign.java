package com.powernode.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdTagReference;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "product-service")
public interface SearchProdTagReferenceFeign {

    @GetMapping("prod/prodTag/getProdTagReferencePageByTagId")
    Page<ProdTagReference> getProdTagReferencePageByTagId(@RequestParam Long tagId,@RequestParam Long size);
}
