package com.powernode.service;

import com.powernode.domain.Basket;
import com.baomidou.mybatisplus.extension.service.IService;
public interface BasketService extends IService<Basket>{


    /**
     * 查询用户购物车中商品数量
     * @param userId
     * @return
     */
    Integer selectUserBasketCount(String userId);
}
