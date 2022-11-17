package com.powernode.config;

import cn.hutool.core.io.FileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 资源服务器
 * 作用：统一token校验
 */
@Configuration
@EnableResourceServer
public class ResourceConfig extends ResourceServerConfigurerAdapter {

    /**
     * token存储方式
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 解析公钥
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //通过类路径获取公钥文件
        ClassPathResource resource = new ClassPathResource("public_key.txt");
        //读取公钥文件
        String publickey = null;
        try {
            publickey = FileUtil.readString(resource.getFile(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //设置认证key
        jwtAccessTokenConverter.setVerifierKey(publickey);
        return jwtAccessTokenConverter;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().disable();//跨域请求
        http.csrf().disable();//关闭跨站攻击
        http.sessionManagement().disable();//关闭会话禁用
        http.authorizeRequests().antMatchers(
                //需要放行的请求swagger,druid等
                "/v2/api-docs",
                "/v3/api-docs",
                "/swagger-resources/configuration/ui",  //用来获取支持的动作
                "/swagger-resources",                   //用来获取api-docs的URI
                "/swagger-resources/configuration/security",//安全选项
                "/webjars/**",
                "/swagger-ui/**",
                "/druid/**",
                "/actuator/**").permitAll().and()
                .authorizeRequests().anyRequest().authenticated();
    }
}