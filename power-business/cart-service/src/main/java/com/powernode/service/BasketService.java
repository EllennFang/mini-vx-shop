package com.powernode.service;

import com.powernode.domain.Basket;
import com.baomidou.mybatisplus.extension.service.IService;
import com.powernode.vo.CartTotalAmount;
import com.powernode.vo.CartVo;

import java.util.List;

public interface BasketService extends IService<Basket>{


    /**
     * 查询用户购物车中商品数量
     * @param userId
     * @return
     */
    Integer selectUserBasketCount(String userId);

    /**
     * 查询用户购物车详情
     * @param userId
     * @return
     */
    CartVo selectUserBasketInfo(String userId);

    /**
     * 计算购物车中商品总金额
     * @param basketIdList
     * @return
     */
    CartTotalAmount calculateUserCartTotalAmount(List<Long> basketIdList);
}
