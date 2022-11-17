package com.powernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.powernode.domain.LoginUser;
import org.apache.ibatis.annotations.Mapper;

/**
* ClassName:UserMapper
* Package:com.powernode.mapper
* Description:
* Date:2022/11/5 14:51
* author:abc
*/
@Mapper
public interface LoginUserMapper extends BaseMapper<LoginUser> {
}