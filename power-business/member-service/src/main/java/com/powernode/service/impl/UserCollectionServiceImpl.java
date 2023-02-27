package com.powernode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.UserCollection;
import com.powernode.mapper.UserCollectionMapper;
import com.powernode.service.UserCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService{

    @Autowired
    private UserCollectionMapper userCollectionMapper;


    @Override
    public void addOrCancelProd(String userId, Long prodId) {
        //根据用户标识和商品标识查询当前状态
        UserCollection userCollection = userCollectionMapper.selectOne(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getProdId, prodId)
        );
        //判断是否存在
        if (ObjectUtil.isNull(userCollection)) {
            //添加收藏
            userCollection = new UserCollection();
            userCollection.setUserId(userId);
            userCollection.setProdId(prodId);
            userCollection.setCreateTime(new Date());
            userCollectionMapper.insert(userCollection);
        } else {
            //删除收藏
            userCollectionMapper.deleteById(userCollection.getId());
        }
    }
}
