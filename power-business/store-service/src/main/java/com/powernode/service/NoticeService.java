package com.powernode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powernode.domain.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
public interface NoticeService extends IService<Notice>{


    /**
     * 多条件分页查询公告列表
     * @param page
     * @param notice
     * @return
     */
    Page<Notice> selectNoticePage(Page<Notice> page, Notice notice);
}
