package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdProp;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ProdPropService extends IService<ProdProp>{


    /**
     *
     * @param page
     * @param prodProp
     * @return
     */
    Page<ProdProp> selectProdSpecPage(Page<ProdProp> page, ProdProp prodProp);
}
