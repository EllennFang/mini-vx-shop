package com.powernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.powernode.domain.SysLoginUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* ClassName:SysUserMapper
* Package:com.powernode.mapper
* Description:
* Date:2022/10/27 9:50
* author:abc
*/
@Mapper
public interface SysLoginUserMapper extends BaseMapper<SysLoginUser> {

    @Select("select DISTINCT t1.perms from sys_menu t1 join sys_role_menu t2 join sys_user_role t3 on (t1.menu_id = t2.menu_id and t2.role_id = t3.role_id)\n" +
            "where t3.user_id = #{userId}")
    List<String> selectAuthsByUserId(Long userId);
}