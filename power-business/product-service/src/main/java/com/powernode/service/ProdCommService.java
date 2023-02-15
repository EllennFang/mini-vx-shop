package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdComm;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ProdCommService extends IService<ProdComm>{


    /**
     * 多条件分页查询商品评论列表
     * @param page
     * @param prodComm
     * @return
     */
    Page<ProdComm> selectProdCommPage(Page<ProdComm> page, ProdComm prodComm);
}
