package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProdTagService extends IService<ProdTag>{


    /**
     * 多条件分页查询分组标签列表
     * @param page
     * @param prodTag
     * @return
     */
    Page<ProdTag> selectProdTagPage(Page<ProdTag> page, ProdTag prodTag);

    /**
     * 查询商品分组标签集合
     * @return
     */
    List<ProdTag> selectFrontProdTagList();

}
