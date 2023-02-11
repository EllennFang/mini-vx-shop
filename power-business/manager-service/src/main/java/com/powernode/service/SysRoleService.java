package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysRoleService extends IService<SysRole>{


    /**
     * 多条件分页查询角色列表
     * @param page
     * @param sysRole
     * @return
     */
    Page<SysRole> selectSysRolePage(Page<SysRole> page, SysRole sysRole);
}
