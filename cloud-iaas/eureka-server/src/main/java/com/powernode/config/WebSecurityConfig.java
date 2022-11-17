package com.powernode.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭跨站请求攻击
        http.csrf().disable();
        //写一个放行的，actuator健康检查的
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        super.configure(http);
    }
}
