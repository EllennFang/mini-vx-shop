package com.powernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.constant.RoleConstant;
import com.powernode.domain.SysRole;
import com.powernode.domain.SysRoleMenu;
import com.powernode.mapper.SysRoleMapper;
import com.powernode.mapper.SysRoleMenuMapper;
import com.powernode.service.SysRoleMenuService;
import com.powernode.service.SysRoleService;
import com.powernode.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "com.powernode.service.impl.SysRoleServiceImpl")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService{

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    @Cacheable(key = RoleConstant.PREFIX_ROLE)
    public List<SysRole> list() {
        return sysRoleMapper.selectList(null);
    }

    @Override
    public Page<SysRole> selectSysRolePage(Page<SysRole> page, SysRole sysRole) {
        return sysRoleMapper.selectPage(page,new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(sysRole.getRoleName()), SysRole::getRoleName,sysRole.getRoleName())
                .orderByDesc(SysRole::getCreateTime)
        );
    }

    @Override
    @CacheEvict(key = RoleConstant.PREFIX_ROLE)
    public boolean save(SysRole sysRole) {
        String userId = AuthUtil.getLoginUserId();
        sysRole.setCreateTime(new Date());
        sysRole.setCreateUserId(Long.valueOf(userId));
        int i = sysRoleMapper.insert(sysRole);
        if (i > 0) {
            Long roleId = sysRole.getRoleId();
            List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();
            //获取权限id集合
            List<Long> menuIdList = sysRole.getMenuIdList();
            if (!CollectionUtils.isEmpty(menuIdList) && menuIdList.size() != 0) {
                //新增角色与权限关系记录
                menuIdList.forEach(menuId -> {
                    SysRoleMenu sysRoleMenu = new SysRoleMenu();
                    sysRoleMenu.setMenuId(menuId);
                    sysRoleMenu.setRoleId(roleId);
                    sysRoleMenuList.add(sysRoleMenu);
                });
                //批量新增
                sysRoleMenuService.saveBatch(sysRoleMenuList);
            }

        }

        return i>0;
    }

    @Override
    public SysRole getById(Serializable id) {
        SysRole sysRole = sysRoleMapper.selectById(id);
        //根据角色id查询权限id集合
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, id)
        );
        if (!CollectionUtils.isEmpty(sysRoleMenuList) && sysRoleMenuList.size() != 0) {
            List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
            sysRole.setMenuIdList(menuIdList);
        }
        return sysRole;
    }

    @Override
    @CacheEvict(key = RoleConstant.PREFIX_ROLE)
    public boolean updateById(SysRole sysRole) {
        Long roleId = sysRole.getRoleId();
        //删除角色原有权限
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId,roleId)
        );

        List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();
        //获取权限id集合
        List<Long> menuIdList = sysRole.getMenuIdList();
        if (!CollectionUtils.isEmpty(menuIdList) && menuIdList.size() != 0) {
            //新增角色与权限关系记录
            menuIdList.forEach(menuId -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenuList.add(sysRoleMenu);
            });
            //批量新增
            sysRoleMenuService.saveBatch(sysRoleMenuList);
        }
        return sysRoleMapper.updateById(sysRole)>0;
    }

    @Override
    @CacheEvict(key = RoleConstant.PREFIX_ROLE)
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return sysRoleMapper.deleteBatchIds(idList) == idList.size();
    }
}
