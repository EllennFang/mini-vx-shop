package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.ProdComm;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.vo.ProdCommOverview;

public interface ProdCommService extends IService<ProdComm>{


    /**
     * 多条件分页查询商品评论列表
     * @param page
     * @param prodComm
     * @return
     */
    Page<ProdComm> selectProdCommPage(Page<ProdComm> page, ProdComm prodComm);

    /**
     * 查询单个商品评论总览信息
     * @param prodId
     * @return
     */
    ProdCommOverview selectProdCommOverview(Long prodId);

    /**
     * 分页查询单个商品的评论
     * @param page
     * @param prodId
     * @param evaluate
     * @return
     */
    Page<ProdComm> selectProdCommPageByProd(Page<ProdComm> page, Long prodId, Long evaluate);
}
