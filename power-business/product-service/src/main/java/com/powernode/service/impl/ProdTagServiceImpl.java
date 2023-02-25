package com.powernode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.constant.TagConstant;
import com.powernode.domain.ProdTag;
import com.powernode.mapper.ProdTagMapper;
import com.powernode.service.ProdTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "com.powernode.service.impl.ProdTagServiceImpl")
public class ProdTagServiceImpl extends ServiceImpl<ProdTagMapper, ProdTag> implements ProdTagService{

    @Autowired
    private ProdTagMapper prodTagMapper;


    @Override
    public Page<ProdTag> selectProdTagPage(Page<ProdTag> page, ProdTag prodTag) {
        //如果like和eq同时存在的情况下，应该优先写eq方法，因为eq方法会创建索引
        return prodTagMapper.selectPage(page,new LambdaQueryWrapper<ProdTag>()
                .eq(ObjectUtil.isNotEmpty(prodTag.getStatus()),ProdTag::getStatus,prodTag.getStatus())
                .like(StringUtils.hasText(prodTag.getTitle()),ProdTag::getTitle,prodTag.getTitle())
                .orderByDesc(ProdTag::getSeq)
        );
    }

    @Override
    @Caching(evict = {
        @CacheEvict(key = TagConstant.TAG_LIST),
        @CacheEvict(key = TagConstant.FRONT_TAG_LIST)
    })
    public boolean save(ProdTag prodTag) {
        prodTag.setCreateTime(new Date());
        prodTag.setUpdateTime(new Date());
        prodTag.setShopId(1L);
        prodTag.setIsDefault(1);
        prodTag.setProdCount(0L);
        return prodTagMapper.insert(prodTag)>0;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = TagConstant.TAG_LIST),
            @CacheEvict(key = TagConstant.FRONT_TAG_LIST)
    })
    public boolean updateById(ProdTag prodTag) {
        prodTag.setUpdateTime(new Date());
        return prodTagMapper.updateById(prodTag)>0;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = TagConstant.TAG_LIST),
            @CacheEvict(key = TagConstant.FRONT_TAG_LIST)
    })
    public boolean removeById(Serializable id) {
        return prodTagMapper.deleteById(id)>0;
    }

    @Override
    @Cacheable(key = TagConstant.TAG_LIST)
    public List<ProdTag> list() {
        return prodTagMapper.selectList(new LambdaQueryWrapper<ProdTag>()
                .eq(ProdTag::getStatus,1)
                .orderByDesc(ProdTag::getSeq)
        );
    }

    @Override
    @Cacheable(key = TagConstant.FRONT_TAG_LIST)
    public List<ProdTag> selectFrontProdTagList() {
        return prodTagMapper.selectList(new LambdaQueryWrapper<ProdTag>()
                .eq(ProdTag::getStatus,1)
                .orderByDesc(ProdTag::getSeq)
        );
    }
}
