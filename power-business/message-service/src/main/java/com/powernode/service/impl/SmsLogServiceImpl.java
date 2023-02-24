package com.powernode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.SmsLog;
import com.powernode.mapper.SmsLogMapper;
import com.powernode.service.SmsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
@Service
public class SmsLogServiceImpl extends ServiceImpl<SmsLogMapper, SmsLog> implements SmsLogService{


    @Autowired
    private SmsLogMapper smsLogMapper;

    @Override
    public void saveJdwxMsg(HashMap map) {
        SmsLog smsLog = SmsLog.builder()
                .userId((String) map.get("userId"))
                .userPhone((String) map.get("phonenum"))
                .content((String) map.get("content"))
                .type(1)
                .recDate(new Date())
                .status(1).build();
        smsLogMapper.insert(smsLog);
    }
}
