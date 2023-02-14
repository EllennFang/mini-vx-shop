package com.powernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.constant.CategoryConstant;
import com.powernode.domain.Category;
import com.powernode.mapper.CategoryMapper;
import com.powernode.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "com.powernode.service.impl.CategoryServiceImpl")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    @Cacheable(key = CategoryConstant.CATEGORY_LIST)
    public List<Category> list() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByDesc(Category::getSeq)
        );
    }


    @Override
    @CacheEvict(key = CategoryConstant.CATEGORY_LIST)
    public boolean save(Category category) {
        //获取parentId
        Long parentId = category.getParentId();
        //判断是否为一级类目
        if (0 == parentId) {
            category.setGrade(1);
        } else {
            category.setGrade(2);
        }
        category.setRecTime(new Date());
        category.setUpdateTime(new Date());
        category.setShopId(1L);
        return categoryMapper.insert(category)>0;
    }
}
