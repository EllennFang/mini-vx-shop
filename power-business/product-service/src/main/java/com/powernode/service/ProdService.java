package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Prod;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ProdService extends IService<Prod>{


    /**
     * 多条件分页查询商品列表
     * @param page
     * @param prod
     * @return
     */
    Page<Prod> selectProdPage(Page<Prod> page, Prod prod);
}
