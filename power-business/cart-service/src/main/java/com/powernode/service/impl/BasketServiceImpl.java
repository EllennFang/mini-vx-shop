package com.powernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.Basket;
import com.powernode.mapper.BasketMapper;
import com.powernode.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket> implements BasketService{

    @Autowired
    private BasketMapper basketMapper;

    @Override
    public Integer selectUserBasketCount(String userId) {
        List<Object> objs = basketMapper.selectObjs(new QueryWrapper<Basket>()
                .select("ifnull(SUM(basket_count),0)")
                .eq("user_id", userId)
        );
        Object count = objs.get(0);
        return Integer.parseInt(count.toString());
    }
}
