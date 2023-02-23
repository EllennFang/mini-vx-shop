package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.UserAddr;
import com.powernode.mapper.UserAddrMapper;
import com.powernode.service.UserAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
@CacheConfig(cacheNames = "com.powernode.service.impl.UserAddrServiceImpl")
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr> implements UserAddrService{


    @Autowired
    private UserAddrMapper userAddrMapper;


    @Override
    @Cacheable(key = "#userId")
    public List<UserAddr> selectUserAddrList(String userId) {
        return userAddrMapper.selectList(new LambdaQueryWrapper<UserAddr>()
                .eq(UserAddr::getUserId,userId)
                .eq(UserAddr::getStatus,1)
                .orderByDesc(UserAddr::getCommonAddr,UserAddr::getCreateTime)
        );
    }

    @Override
    @CacheEvict(key = "#userAddr.userId")
    public boolean save(UserAddr userAddr) {
        //查询用户收货地址列表
        List<UserAddr> userAddrList = userAddrMapper.selectList(new LambdaQueryWrapper<UserAddr>()
                .eq(UserAddr::getUserId, userAddr.getUserId())
                .eq(UserAddr::getStatus, 1)
        );
        userAddr.setCommonAddr(0);
        userAddr.setStatus(1);
        userAddr.setCreateTime(new Date());
        userAddr.setUpdateTime(new Date());
        userAddr.setVersion(0);
        //判断当前用户是否有收货地址
        if (CollectionUtil.isEmpty(userAddrList) || userAddrList.size() == 0) {
            //说明：用户没有收货地址，那当前新增的就为默认收货地址
            userAddr.setCommonAddr(1);
        }
        return userAddrMapper.insert(userAddr)>0;
    }
}
