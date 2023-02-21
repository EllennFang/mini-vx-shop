package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powernode.constant.AuthConstant;
import com.powernode.domain.LoginUser;
import com.powernode.domain.SysLoginUser;
import com.powernode.mapper.LoginUserMapper;
import com.powernode.mapper.SysLoginUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysLoginUserMapper sysLoginUserMapper;

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.secret}")
    private String secret;
    @Value("${wx.url}")
    private String url;


    /**
     * 根据用户名查询数据库的方法
     *  （因为我们有2个前端，所以要区分开来）
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取request请求对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取请求头中的用户类型（后台管理员or会员）
        String loginType = request.getHeader(AuthConstant.LOGIN_TYPE);
        //判断是否为空
        if (StrUtil.isEmpty(loginType)) {
            return null;
        }
        //判断用户类型
        switch (loginType) {
            case AuthConstant.SYS_USER:
                //后台管理员用户
                //根据用户名查询用户
                SysLoginUser sysLoginUser = sysLoginUserMapper.selectOne(new LambdaQueryWrapper<SysLoginUser>()
                        .eq(SysLoginUser::getUsername, username)
                );
                //判断用户是否存在
                if (ObjectUtil.isNotNull(sysLoginUser)) {
                    //后台管理员有权限，查询当前登录管理员的权限
                    List<String> auths = sysLoginUserMapper.selectAuthsByUserId(sysLoginUser.getUserId());
                    if (CollectionUtil.isNotEmpty(auths) && auths.size() != 0) {
                        //将查询出来的权限封装到用户对象中
                        sysLoginUser.setAuths(auths);
                    }
                }
                return sysLoginUser;
            case AuthConstant.MEMBER:
                //调用微信 登录凭证校验接口code2Session 来获取 用户唯一标识 OpenID
//                String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+xxx+"&secret="+xxxx+"&js_code="+username+"&grant_type=authorization_code";
                String realUrl = String.format(url, appid, secret, username);
                //调用接口，响应json格式的字符串数据
                String resultJsonStr = restTemplate.getForObject(realUrl, String.class);
                //解析json格式的字符串
                JSONObject jsonObject = JSONObject.parseObject(resultJsonStr);
                //获取微信用户唯一标识openid
                String openid = jsonObject.getString("openid");
                if (!StringUtils.hasText(openid)) {
                    return null;
                }
                //应该去建立自己系统的用户体系
                //我们需要根据微信openid来绑定用户是否已经存在于我们用户体系内
                LoginUser loginUser = loginUserMapper.selectOne(new LambdaQueryWrapper<LoginUser>()
                        .eq(LoginUser::getUserId, openid)
                );
                //如果存在：直接登录
                if (ObjectUtil.isNull(loginUser)) {
                    //如果不存在：创建用户
                    loginUser = createLoginUser(openid,request);
                }
                return loginUser;
        }
        return null;
    }

    private LoginUser createLoginUser(String openid, HttpServletRequest request) {
        //获取远程ipf址地
        String remoteHost = request.getRemoteHost();
        LoginUser loginUser = LoginUser.builder()
                .userId(openid)
                .userRegtime(new Date())
                .modifyTime(new Date())
                .userLasttime(new Date())
                .userLastip(remoteHost)
                .userRegip(remoteHost)
                .status(1).build();
        //插入数据库
        loginUserMapper.insert(loginUser);
        return loginUser;
    }
}
