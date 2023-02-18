package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.PickAddr;
import com.baomidou.mybatisplus.extension.service.IService;
public interface PickAddrService extends IService<PickAddr>{


    /**
     * 多条件分页查询自提点列表
     * @param page
     * @param pickAddr
     * @return
     */
    Page<PickAddr> selectPickAddrPage(Page<PickAddr> page, PickAddr pickAddr);
}
