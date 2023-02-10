package com.powernode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.constant.RoleConstant;
import com.powernode.domain.SysRole;
import com.powernode.mapper.SysRoleMapper;
import com.powernode.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "com.powernode.service.impl.SysRoleServiceImpl")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService{

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    @Cacheable(key = RoleConstant.PREFIX_ROLE)
    public List<SysRole> list() {
        return sysRoleMapper.selectList(null);
    }
}
