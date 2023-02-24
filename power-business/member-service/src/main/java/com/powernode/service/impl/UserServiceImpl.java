package com.powernode.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.constant.QueueConstant;
import com.powernode.domain.User;
import com.powernode.mapper.UserMapper;
import com.powernode.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void send(Map<String, Object> map) {
        //组装数据
        String phonenum = (String) map.get("phonenum");
        map.put("appkey","xxxxxx");
        map.put("mobile",phonenum);
        //短信内容 = 签名 + 短信正文
        String code = RandomUtil.randomNumbers(4);
        //将生成的随机数字存放到redis缓存中
        stringRedisTemplate.opsForValue().set(phonenum,code, Duration.ofMinutes(5));
        String content = "【创信】你的验证码是："+code+"，3分钟内有效！";
        map.put("content",content);

        //并将组装好的数据存放到消息队列中
        rabbitTemplate.convertAndSend(QueueConstant.PHONE_CODE_QUEUE, JSON.toJSONString(map));
    }

    @Override
    public void savePhone(Map<String, Object> map) {
        String phonenum = (String) map.get("phonenum");
        String userId = (String) map.get("userId");
        //获取用户输入的验证码
        String code = (String) map.get("code");
        //获取缓存中的验证码
        String redisCode = stringRedisTemplate.opsForValue().get(phonenum);
        //验证用户短信验证码是否正确
        if (!code.equals(redisCode)) {
            throw new RuntimeException("验证码不正确");
        }
        //更新手机号码
        User user = new User();
        user.setUserId(userId);
        user.setUserMobile(phonenum);
        userMapper.update(user,new LambdaQueryWrapper<User>()
                .eq(User::getUserId,userId)
        );
    }
}
