package com.powernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.powernode.domain.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}