package com.powernode.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.powernode.constant.GatewayConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * feign拦截器：做一个token的传递
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

    /**
     * 拦截后，要做处理的方式
     * 1.浏览器---token--->A服务----token---->B服务
     * 2.A服务----token---->B服务(mq监听中发起的服务)
     * 3.第三方服务讲求我，这个接口必须放行
     *   第三方服务---->A服务---->B服务
     * @param requestTemplate 就是即将要发起的请求对象（无token）
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取上一次的请求对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //判断是否有值
        if (ObjectUtil.isNotNull(requestAttributes)) {
            HttpServletRequest request = requestAttributes.getRequest();
            //获取请求头的token(bearer jwt)
            String authorization = request.getHeader(GatewayConstant.AUTHORIZATION);
            //判断请求头中是否有authorization
            if (StrUtil.isNotEmpty(authorization)) {
                //将上次请求头中token存放到当前请求头中
                requestTemplate.header(GatewayConstant.AUTHORIZATION,authorization);
                return;
            }
        }
        //设置永久token
        requestTemplate.header(GatewayConstant.AUTHORIZATION,GatewayConstant.BEARER+"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhbGwiXSwiZXhwIjozODE2MTUxMTMyLCJqdGkiOiI3ODE1YTcyMi00ZDIyLTQyNGEtODY3NS1kN2EwY2U3NmM0MWYiLCJjbGllbnRfaWQiOiJjbGllbnQifQ.SySxTQA9A9vjAmt2dlEYeRMjJSsPrCypjYkhYlsFZ7yJNDqBM6CYvKlN4goKmcZqAcfgY9a2tztWbR_FiGnoeqZgJFmaJqQupUgNS6aVbhf-CyhPBMQouOslpQawG-zlfybWgj2g_MHCZOSQFjyxQ9O-qsb6ehKKStIOl_3zxP-JEhEva15TA03qYDbaqcCJqPtsTp8Lo5BxMoD4OOsS96Xkd-vmbOrmi-kmTTHNHUlp9Cd1wF7rVCxjBNkfMxcS0VY5q4GAl2MsIrIHcMaOXL3CK3teQ5hN_aE7FHdjAWTYU2_ylUxmITJFisKM1wD7C3NUrzowIlLaCU3UZBEJRQ");
    }
}