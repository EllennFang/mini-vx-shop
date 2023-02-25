package com.powernode.feign.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdTagReference;
import com.powernode.feign.SearchProdTagReferenceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SearchProdTagReferenceFeignHystrix implements SearchProdTagReferenceFeign {

    @Override
    public Page<ProdTagReference> getProdTagReferencePageByTagId(Long tagId, Long size) {
        log.error("远程调用：根据商品分组标签id查询商品分组标签关系失败");
        return null;
    }
}
