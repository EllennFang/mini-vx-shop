package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.ProdProp;
import com.powernode.domain.ProdPropValue;
import com.powernode.mapper.ProdPropMapper;
import com.powernode.mapper.ProdPropValueMapper;
import com.powernode.service.ProdPropService;
import com.powernode.service.ProdPropValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdPropServiceImpl extends ServiceImpl<ProdPropMapper, ProdProp> implements ProdPropService{

    @Autowired
    private ProdPropMapper prodPropMapper;

    @Autowired
    private ProdPropValueMapper prodPropValueMapper;

    @Autowired
    private ProdPropValueService prodPropValueService;


    @Override
    public Page<ProdProp> selectProdSpecPage(Page<ProdProp> page, ProdProp prodProp) {
        //分页查询商品规格列表
        page = prodPropMapper.selectPage(page,new LambdaQueryWrapper<ProdProp>()
                .like(StringUtils.hasText(prodProp.getPropName()),ProdProp::getPropName,prodProp.getPropName())
        );
        //获取商品规格记录
        List<ProdProp> prodPropList = page.getRecords();
        //判断是否有值
        if (CollectionUtil.isEmpty(prodPropList) || prodPropList.size() == 0) {
            return page;
        }
        //从商品属性集合中获取商品属性id集合
        List<Long> propIdList = prodPropList.stream().map(ProdProp::getPropId).collect(Collectors.toList());
        //根据商品属性id集合查询商品属性值集合
        List<ProdPropValue> prodPropValueList = prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>()
                .in(ProdPropValue::getPropId, propIdList)
        );
        //循环遍历商品属性集合
        prodPropList.forEach(prodProp1 -> {
            //从商品属性值集合中过滤出与当前商品属性id一致的商品属性值集合
            List<ProdPropValue> prodPropValues = prodPropValueList.stream()
                    .filter(prodPropValue -> prodPropValue.getPropId().equals(prodProp1.getPropId()))
                    .collect(Collectors.toList());
            //组装数据
            prodProp1.setProdPropValues(prodPropValues);
        });
        return page;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean save(ProdProp prodProp) {
        //新增商品属性
        prodProp.setRule(2);
        prodProp.setShopId(1L);
        int i = prodPropMapper.insert(prodProp);
        if (i > 0) {
            //新增商品属性值
            //获取商品属性值集合对象
            List<ProdPropValue> prodPropValueList = prodProp.getProdPropValues();
            if (CollectionUtil.isEmpty(prodPropValueList) || prodPropValueList.size() == 0) {
                throw new RuntimeException("服务器开小差了");
            }
            Long propId = prodProp.getPropId();
            //循环遍历
            prodPropValueList.forEach(prodPropValue -> {
                prodPropValue.setPropId(propId);
            });
            //批量添加商品属性值集合
            prodPropValueService.saveBatch(prodPropValueList);
        }

        return i>0;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean updateById(ProdProp prodProp) {
        Long propId = prodProp.getPropId();
        //获取商品属性值集合
        List<ProdPropValue> prodPropValueList = prodProp.getProdPropValues();
        if (CollectionUtil.isEmpty(prodPropValueList) || prodPropValueList.size() == 0) {
            throw new RuntimeException("服务器开小差了");
        }
        //删除原有的商品属性值集合
        prodPropValueMapper.delete(new LambdaQueryWrapper<ProdPropValue>()
                .eq(ProdPropValue::getPropId,propId)
        );
        //批量添加商品属性值集合
        prodPropValueService.saveBatch(prodPropValueList);

        return prodPropMapper.updateById(prodProp)>0;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean removeById(Serializable id) {
        //删除属性值
        prodPropValueMapper.delete(new LambdaQueryWrapper<ProdPropValue>()
                .eq(ProdPropValue::getPropId,id)
        );
        return prodPropMapper.deleteById(id)>0;
    }
}
