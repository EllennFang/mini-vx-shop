package com.powernode.service;

import com.powernode.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMenuService extends IService<SysMenu>{


    /**
     * 根据用户标识查询用户菜单集合
     * @param userId
     * @return
     */
    List<SysMenu> selectUserMenuList(String userId);
}
