package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Prod;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.model.ChangeStock;

public interface ProdService extends IService<Prod>{


    /**
     * 多条件分页查询商品列表
     * @param page
     * @param prod
     * @return
     */
    Page<Prod> selectProdPage(Page<Prod> page, Prod prod);

    /**
     * 根据标识查询商品详情和商品sku
     * @param prodId
     * @return
     */
    Prod selectProdAndSkuDetailById(Long prodId);

    /**
     * 修改商品prod和sku库存数量
     * @param changeStock
     */
    void changeStock(ChangeStock changeStock);
}
