package com.powernode.service;

import com.powernode.domain.UserAddr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAddrService extends IService<UserAddr>{


    /**
     * 查询用户收货地址列表
     * @param userId
     * @return
     */
    List<UserAddr> selectUserAddrList(String userId);
}
