package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.IndexImg;
import com.baomidou.mybatisplus.extension.service.IService;
public interface IndexImgService extends IService<IndexImg>{


    /**
     * 多条件查询轮播图列表
     * @param page
     * @param indexImg
     * @return
     */
    Page<IndexImg> selectIndexImgPage(Page<IndexImg> page, IndexImg indexImg);
}
