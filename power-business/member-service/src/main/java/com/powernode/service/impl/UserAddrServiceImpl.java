package com.powernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.UserAddr;
import com.powernode.mapper.UserAddrMapper;
import com.powernode.service.UserAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
        );
    }
}
