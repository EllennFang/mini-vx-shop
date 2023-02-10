package com.powernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.SysUser;
import com.powernode.domain.SysUserRole;
import com.powernode.mapper.SysUserMapper;
import com.powernode.mapper.SysUserRoleMapper;
import com.powernode.service.SysUserRoleService;
import com.powernode.service.SysUserService;
import com.powernode.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public Page<SysUser> selectSysUserPage(Page<SysUser> page, SysUser sysUser) {
        return sysUserMapper.selectPage(page,new LambdaQueryWrapper<SysUser>()
                //username like '%'#{sysUser.username}'%'
                .like(StringUtils.hasText(sysUser.getUsername()),SysUser::getUsername,sysUser.getUsername())
                .orderByDesc(SysUser::getCreateTime)
                //<if test="username != null and username != ''">
                // username like '%'#{sysUser.username}'%'
                // </if>
        );
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean save(SysUser sysUser) {
        //新增管理员
        //密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUser.setCreateTime(new Date());
        sysUser.setCreateUserId(Long.valueOf(AuthUtil.getLoginUserId()));
        int i = sysUserMapper.insert(sysUser);
        if (i > 0) {
            //获取用户角色id集合
            List<Long> roleIdList = sysUser.getRoleIdList();
            //判断是否有角色
            if (!CollectionUtils.isEmpty(roleIdList) && roleIdList.size() != 0) {
                //新增用户角色记录
                List<SysUserRole> sysUserRoleList = new ArrayList<>();
                //循环遍历用户角色id集合
                roleIdList.forEach(roleId -> {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setRoleId(roleId);
                    sysUserRole.setUserId(sysUser.getUserId());
                    sysUserRoleList.add(sysUserRole);
                });
                sysUserRoleService.saveBatch(sysUserRoleList);
            }
        }
        return i>0;
    }

    @Override
    public SysUser getById(Serializable id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        //根据用户标识查询角色集合
        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id)
        );
        //判断是否有值
        if (!CollectionUtils.isEmpty(sysUserRoleList) && sysUserRoleList.size() != 0) {
            //获取角色id集合
            List<Long> roleIdList = sysUserRoleList.stream().map(SysUserRole::getUserId).collect(Collectors.toList());
            sysUser.setRoleIdList(roleIdList);
        }
        return sysUser;
    }
}
