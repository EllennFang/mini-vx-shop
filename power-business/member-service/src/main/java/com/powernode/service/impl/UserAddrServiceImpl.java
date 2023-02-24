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
import org.springframework.transaction.annotation.Transactional;

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


    @Override
    @CacheEvict(key = "#userAddr.userId")
    public boolean updateById(UserAddr userAddr) {
        userAddr.setUpdateTime(new Date());
        return userAddrMapper.updateById(userAddr)>0;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    @CacheEvict(key = "#userId")
    public void deleteUserAddr(String userId, Long addrId) {

        //查询当前用户的默认收货地址
        UserAddr userAddr = userAddrMapper.selectOne(new LambdaQueryWrapper<UserAddr>()
                .eq(UserAddr::getUserId, userId)
                .eq(UserAddr::getCommonAddr, 1)
        );
        //不是：删除
        userAddrMapper.deleteById(addrId);

        //获取默认收货地址标识
        Long defaultAddrId = userAddr.getAddrId();
        //判断当前删除的收货地址是否为默认收货地址
        if (defaultAddrId == addrId) {
            //是：再去重新找一个新的收货地址并设置为默认收货地址
            //把最近添加的收货地址设置为默认收货地址
            List<UserAddr> userAddrList = userAddrMapper.selectList(new LambdaQueryWrapper<UserAddr>()
                    .eq(UserAddr::getUserId, userId)
                    .eq(UserAddr::getStatus, 1)
                    .orderByDesc(UserAddr::getCreateTime)
            );
            //判断是否有值
            if (CollectionUtil.isNotEmpty(userAddrList) && userAddrList.size() != 0) {
                UserAddr defaultUserAddr = userAddrList.get(0);
                defaultUserAddr.setCommonAddr(1);
                userAddrMapper.updateById(defaultUserAddr);
            }
        }

    }
}
