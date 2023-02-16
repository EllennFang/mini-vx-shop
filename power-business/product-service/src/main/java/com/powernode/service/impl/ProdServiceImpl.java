package com.powernode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.Prod;
import com.powernode.mapper.ProdMapper;
import com.powernode.service.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod> implements ProdService{

    @Autowired
    private ProdMapper prodMapper;

    @Override
    public Page<Prod> selectProdPage(Page<Prod> page, Prod prod) {
        return prodMapper.selectPage(page,new LambdaQueryWrapper<Prod>()
                .eq(ObjectUtil.isNotEmpty(prod.getStatus()),Prod::getStatus,prod.getStatus())
                .like(StringUtils.hasText(prod.getProdName()),Prod::getProdName,prod.getProdName())
                .orderByDesc(Prod::getPutawayTime,Prod::getCreateTime)
        );
    }
}
