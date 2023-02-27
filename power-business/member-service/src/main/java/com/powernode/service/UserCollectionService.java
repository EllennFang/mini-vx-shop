package com.powernode.service;

import com.powernode.domain.UserCollection;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserCollectionService extends IService<UserCollection>{


    /**
     * 用户收藏或取消收藏商品
     * @param userId
     * @param prodId
     */
    void addOrCancelProd(String userId, Long prodId);
}
