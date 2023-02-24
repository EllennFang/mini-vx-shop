package com.powernode.service;

import com.powernode.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserService extends IService<User>{


    /**
     * 获取短信验证码
     * @param map
     */
    void send(Map<String, Object> map);

    /**
     * 绑定用户手机号码
     * @param map
     */
    void savePhone(Map<String, Object> map);
}
