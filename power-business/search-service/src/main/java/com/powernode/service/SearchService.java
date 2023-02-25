package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Prod;

public interface SearchService {

    /**
     * 根据分组标签查询商品集合
     * @param page
     * @param tagId
     * @return
     */
    Page<Prod> selectProdListByTagId(Page<Prod> page, Long tagId);

}
