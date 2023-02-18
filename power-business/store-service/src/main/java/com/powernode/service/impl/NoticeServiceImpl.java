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

import java.util.Date;

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

    @Override
    public boolean save(Notice notice) {
        notice.setUpdateTime(new Date());
        Integer status = notice.getStatus();
        if (1 == status) {
            notice.setPublishTime(new Date());
        }
        return noticeMapper.insert(notice)>0;
    }

    @Override
    public boolean updateById(Notice notice) {
        notice.setUpdateTime(new Date());
        return noticeMapper.updateById(notice)>0;
    }
}
