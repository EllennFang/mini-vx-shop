package com.powernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.PickAddr;
import com.powernode.mapper.PickAddrMapper;
import com.powernode.service.PickAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PickAddrServiceImpl extends ServiceImpl<PickAddrMapper, PickAddr> implements PickAddrService{

    @Autowired
    private PickAddrMapper pickAddrMapper;

    @Override
    public Page<PickAddr> selectPickAddrPage(Page<PickAddr> page, PickAddr pickAddr) {
        return pickAddrMapper.selectPage(page,new LambdaQueryWrapper<PickAddr>()
                .like(StringUtils.hasText(pickAddr.getAddrName()),PickAddr::getAddrName,pickAddr.getAddrName())
                .orderByDesc(PickAddr::getProvinceId,PickAddr::getCityId,PickAddr::getAreaId)
        );
    }
}
