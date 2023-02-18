package com.powernode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.IndexImg;
import com.powernode.mapper.IndexImgMapper;
import com.powernode.service.IndexImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class IndexImgServiceImpl extends ServiceImpl<IndexImgMapper, IndexImg> implements IndexImgService{


    @Autowired
    private IndexImgMapper indexImgMapper;


    @Override
    public Page<IndexImg> selectIndexImgPage(Page<IndexImg> page, IndexImg indexImg) {
        return indexImgMapper.selectPage(page,new LambdaQueryWrapper<IndexImg>()
                .eq(ObjectUtil.isNotEmpty(indexImg.getStatus()),IndexImg::getStatus,indexImg.getStatus())
                .orderByDesc(IndexImg::getSeq)
        );
    }
}
