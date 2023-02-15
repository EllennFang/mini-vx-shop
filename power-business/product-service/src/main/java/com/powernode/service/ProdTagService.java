package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdTag;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ProdTagService extends IService<ProdTag>{


    /**
     * 多条件分页查询分组标签列表
     * @param page
     * @param prodTag
     * @return
     */
    Page<ProdTag> selectProdTagPage(Page<ProdTag> page, ProdTag prodTag);
}
