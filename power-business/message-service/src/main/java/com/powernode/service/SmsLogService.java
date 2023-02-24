package com.powernode.service;

import com.powernode.domain.SmsLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;

public interface SmsLogService extends IService<SmsLog>{


    /**
     * 记录短信内容
     * @param map
     */
    void saveJdwxMsg(HashMap map);
}
