package com.powernode.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
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

        }
        return null;
    }

    private void createLoginUser(String openid, HttpServletRequest request) {
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
    }
}