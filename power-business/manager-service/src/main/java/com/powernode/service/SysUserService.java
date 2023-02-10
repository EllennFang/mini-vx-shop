package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysUserService extends IService<SysUser>{


    /**
     * 多条件分页查询管理员列表
     * @param page
     * @param sysUser
     * @return
     */
    Page<SysUser> selectSysUserPage(Page<SysUser> page, SysUser sysUser);
}
