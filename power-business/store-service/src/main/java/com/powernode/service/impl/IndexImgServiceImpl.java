package com.powernode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.constant.IndexImgConstant;
import com.powernode.domain.IndexImg;
import com.powernode.domain.Prod;
import com.powernode.feign.IndexImgProdFeign;
import com.powernode.mapper.IndexImgMapper;
import com.powernode.service.IndexImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "com.powernode.service.impl.IndexImgServiceImpl")
public class IndexImgServiceImpl extends ServiceImpl<IndexImgMapper, IndexImg> implements IndexImgService{


    @Autowired
    private IndexImgMapper indexImgMapper;

    @Autowired
    private IndexImgProdFeign indexImgProdFeign;


    @Override
    public Page<IndexImg> selectIndexImgPage(Page<IndexImg> page, IndexImg indexImg) {
        return indexImgMapper.selectPage(page,new LambdaQueryWrapper<IndexImg>()
                .eq(ObjectUtil.isNotEmpty(indexImg.getStatus()),IndexImg::getStatus,indexImg.getStatus())
                .orderByDesc(IndexImg::getSeq)
        );
    }

    @Override
    @CacheEvict(key = IndexImgConstant.FRONT_INDEX_IMG_LIST)
    public boolean save(IndexImg indexImg) {
        //获取类型
        Integer type = indexImg.getType();
        if (type == -1) {
            indexImg.setRelation(-1L);
        }
        //获取状态
        Integer status = indexImg.getStatus();
        if (1 == status) {
            indexImg.setUploadTime(new Date());
        }
        return indexImgMapper.insert(indexImg)>0;
    }

    @Override
    public IndexImg getById(Serializable id) {
        //根据标识查询轮播图信息
        IndexImg indexImg = indexImgMapper.selectById(id);
        //获取轮播图类型type,-1未关联，0关联
        Integer type = indexImg.getType();
        if (0 == type) {
            //远程调用：获取商品的信息
            Long prodId = indexImg.getRelation();
            Prod prod = indexImgProdFeign.getProdById(prodId);
            indexImg.setPic(prod.getPic());
            indexImg.setProdName(prod.getProdName());
        }
        return indexImg;
    }

    @Override
    @CacheEvict(key = IndexImgConstant.FRONT_INDEX_IMG_LIST)
    public boolean updateById(IndexImg indexImg) {
        //获取状态
        Integer status = indexImg.getStatus();
        if (1 == status) {
            indexImg.setUploadTime(new Date());
        }
        //获取轮播图类型type,-1未关联，0关联
        Integer type = indexImg.getType();
        if (-1 == type) {
            indexImg.setRelation(-1L);
        }
        return indexImgMapper.updateById(indexImg)>0;
    }

    @Override
    @CacheEvict(key = IndexImgConstant.FRONT_INDEX_IMG_LIST)
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        return indexImgMapper.deleteBatchIds(idList) == idList.size();
    }

    @Override
    @Cacheable(key = IndexImgConstant.FRONT_INDEX_IMG_LIST)
    public List<IndexImg> list() {
        return indexImgMapper.selectList(new LambdaQueryWrapper<IndexImg>()
                .eq(IndexImg::getStatus,1)
                .orderByDesc(IndexImg::getSeq)
        );
    }
}
