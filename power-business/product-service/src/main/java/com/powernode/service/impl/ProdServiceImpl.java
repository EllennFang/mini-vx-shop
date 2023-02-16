package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.Prod;
import com.powernode.domain.ProdTagReference;
import com.powernode.domain.Sku;
import com.powernode.mapper.ProdMapper;
import com.powernode.service.ProdService;
import com.powernode.service.ProdTagReferenceService;
import com.powernode.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod> implements ProdService{

    @Autowired
    private ProdMapper prodMapper;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProdTagReferenceService prodTagReferenceService;

    @Override
    public Page<Prod> selectProdPage(Page<Prod> page, Prod prod) {
        return prodMapper.selectPage(page,new LambdaQueryWrapper<Prod>()
                .eq(ObjectUtil.isNotEmpty(prod.getStatus()),Prod::getStatus,prod.getStatus())
                .like(StringUtils.hasText(prod.getProdName()),Prod::getProdName,prod.getProdName())
                .orderByDesc(Prod::getPutawayTime,Prod::getCreateTime)
        );
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean save(Prod prod) {
        //新增商品
        Integer status = prod.getStatus();
        prod.setShopId(1L);
        if (1 == status) {
            prod.setPutawayTime(new Date());
        }
        Prod.DeliveryModeVo deliveryModeVo = prod.getDeliveryModeVo();
        prod.setDeliveryMode(JSON.toJSONString(deliveryModeVo));
        prod.setSoldNum(0);
        prod.setCreateTime(new Date());
        prod.setUpdateTime(new Date());
        prod.setVersion(0);
        int i = prodMapper.insert(prod);
        //商品标识
        Long prodId = prod.getProdId();
        if (i > 0) {
            //新增商品与分组标签的关系记录
            List<Long> tagIdList = prod.getTagList();
            //判断是否有值
            if (CollectionUtil.isNotEmpty(tagIdList) && tagIdList.size() != 0) {
                List<ProdTagReference> prodTagReferenceList = new ArrayList<>();
                //循环商品分组标签id集合
                tagIdList.forEach(tagId -> {
                    //创建商品与分组标签关系记录
                    ProdTagReference prodTagReference = new ProdTagReference();
                    prodTagReference.setProdId(prodId);
                    prodTagReference.setTagId(tagId);
                    prodTagReference.setCreateTime(new Date());
                    prodTagReference.setShopId(1L);
                    prodTagReference.setStatus(1);
                    //收集关系记录
                    prodTagReferenceList.add(prodTagReference);
                });
                //批量添加
                prodTagReferenceService.saveBatch(prodTagReferenceList);
            }

            //获取商品sku对象集合
            List<Sku> skuList = prod.getSkuList();
            //判断商品sku对象集合是否有值
            if (CollectionUtil.isNotEmpty(skuList) && skuList.size() != 0) {
                //修改原始库存数量
                //循环遍历商品sku对象集合
                skuList.forEach(sku -> {
                    //获取商品sku库存数量
                    Integer stocks = sku.getStocks();
                    sku.setProdId(prodId);
                    sku.setActualStocks(stocks);
                    sku.setStocks(0);
                    sku.setRecTime(new Date());
                    sku.setUpdateTime(new Date());
                    sku.setStatus(1);
                    sku.setIsDelete(0);
                });
                //批量添加
                skuService.saveBatch(skuList);
            }
        }


        return i > 0;
    }
}
