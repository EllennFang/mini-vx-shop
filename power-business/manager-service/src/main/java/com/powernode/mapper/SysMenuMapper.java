package com.powernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.powernode.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("select  DISTINCT t1.*   from sys_menu t1 join sys_role_menu t2 join sys_user_role t3\n" +
            "on (t1.menu_id = t2.menu_id and t2.role_id = t3.role_id)\n" +
            "where t3.user_id = #{userId} and (t1.type = 0 or t1.type = 1)")
    List<SysMenu> selectUserMenuList(String userId);
}
