package com.powernode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.Notice;
import com.powernode.mapper.NoticeMapper;
import com.powernode.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService{


    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public Page<Notice> selectNoticePage(Page<Notice> page, Notice notice) {
        return noticeMapper.selectPage(page,new LambdaQueryWrapper<Notice>()
                .eq(ObjectUtil.isNotEmpty(notice.getStatus()),Notice::getStatus,notice.getStatus())
                .eq(ObjectUtil.isNotEmpty(notice.getIsTop()),Notice::getIsTop,notice.getIsTop())
                .like(StringUtils.hasText(notice.getTitle()),Notice::getTitle,notice.getTitle())
                .orderByDesc(Notice::getIsTop,Notice::getPublishTime)
        );
    }
}
