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
import org.springframework.util.CollectionUtils;

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

    @Override
    @CacheEvict(key = CategoryConstant.CATEGORY_LIST)
    public boolean updateById(Category category) {

        //获取当前类目原有的级别
        Category oldCategory = categoryMapper.selectById(category.getCategoryId());
        Integer oldGrade = oldCategory.getGrade();
        //获取当前级别
        Integer nowGrade = category.getGrade();
        category.setUpdateTime(new Date());
        //判断类目是否修改级别
        if (oldGrade == 1 && nowGrade == 2) {
            //1变2，查询原来一级类目下没有子类目才可变更
            List<Category> child = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                    .eq(Category::getParentId, oldCategory.getCategoryId())
            );
            if (!CollectionUtils.isEmpty(child) && child.size() != 0) {
                throw new RuntimeException("不可修改");
            }
        } else {
            //2变1
            category.setParentId(0L);
        }

        return categoryMapper.updateById(category)>0;
    }
}
