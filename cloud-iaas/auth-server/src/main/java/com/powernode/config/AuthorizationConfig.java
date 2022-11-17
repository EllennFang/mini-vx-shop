package com.powernode.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * 授权服务配置类
 * 1.配置token使用的存储方式
 * 2.创建jwt转换器（使用非对称方式）
 * 3.第三方应用的配置（前端应用都为第三方应用）
 * 4.暴露端点信息
 * 加密器
 */
@Configuration
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;


    /**
     * TokenStore即它就是用来保存token的
     * TokenStore的实现类有如下几种：
     * 1>InMemoryTokenStore：默认保存 ,把Token 存在内存中
     * 2>JdbcTokenStore：把access_token存在数据库中
     * 3>JwkTokenStore：将 access_token 保存到 JSON Web Key
     * 4>JwtTokenStore：JWT这种方式比较特殊，这是一种无状态方式的存储，
     *                  不进行内存、数据库存储，只是JWT中携带全面的用户信息，保存在jwt中携带过去校验就可以
     * 5>RedisTokenStore：把Token 存在 Redis中
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 创建jwt转换器，使用非对称加密
     * @return
     */
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //读取私钥
        ClassPathResource resource = new ClassPathResource("power-jwt.jks");
        //创建钥匙工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "power123".toCharArray());
        //从钥匙工厂中通过别名获取钥匙
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("power-jwt");
        //设置钥匙
        jwtAccessTokenConverter.setKeyPair(keyPair);
        return jwtAccessTokenConverter;
    }

    /**
     * 配置第三方应用和授权规则
     * 1.密码授权->主要是给前端应用授权
     * 2.客户端授权->主要是给服务之间授权
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("web")//给前端应用授权方式配置
                .secret(passwordEncoder().encode("web-secret"))
                .scopes("all")
                .authorizedGrantTypes("password")   //password授权方式
                .accessTokenValiditySeconds(60*60*4)//前端token的过期时间4小时
                .redirectUris("https://www.bjpowernode.com")
                .and()
                .withClient("client")
                .secret(passwordEncoder().encode("client-secret"))
                .scopes("all")
                .authorizedGrantTypes("client_credentials")
                .accessTokenValiditySeconds(Integer.MAX_VALUE)//服务与服务之间授权没有过期时间
                .redirectUris("https://www.bjpowernode.com");

    }

    /**
     * 暴露端点
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter())
                .authenticationManager(authenticationManager);//由于使用密码授权，所以需要一个密码认证管理器
    }

    /**
     * 密码加密器
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
