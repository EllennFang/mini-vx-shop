package com.powernode.aspect;

import com.alibaba.fastjson.JSON;
import com.powernode.anno.Log;
import com.powernode.domain.SysLog;
import com.powernode.domain.SysUser;
import com.powernode.service.SysLogService;
import com.powernode.service.SysUserService;
import com.powernode.utils.AuthUtil;
import com.powernode.utils.ManagerThreadPool;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class LogAspect {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysLogService sysLogService;

    @Around(value = "@annotation(com.powernode.anno.Log)")
    public Object logAround(ProceedingJoinPoint joinPoint) {
        Object result = null;
        //获取目标方法参数
        Object[] args = joinPoint.getArgs();
        //获取目标方法的对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取目标方法对象
        Method method = signature.getMethod();
        //获取目标方法名称
        String methodName = method.toString();
        //获取目标方法上的注解
        Log logAnnotation = method.getAnnotation(Log.class);
        //获取注解的属性
        String operation = logAnnotation.operation();
        //获取ip地址
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String remoteHost = request.getRemoteHost();
        //开始时间
        long start = System.currentTimeMillis();
        try {
            //执行目标方法
            result = joinPoint.proceed(args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //结束时间
        long end = System.currentTimeMillis();
        //方法执行时长
        long execTime = end - start;
        //获取用户id
        String userId = AuthUtil.getLoginUserId();
        //创建多线程
        ManagerThreadPool.poolExecutor.execute(() -> {
            //根据用户标识查询用户信息
            SysUser sysUser = sysUserService.getById(userId);
            //创建操作形为日志对象
            SysLog sysLog = SysLog.builder()
                    .createDate(new Date())
                    .ip(remoteHost)
                    .method(methodName)
                    .operation(operation)
                    .params(args == null ? "" : JSON.toJSONString(args))
                    .time(execTime)
                    .username(sysUser.getUsername()).build();
            //插入形为日志
            sysLogService.save(sysLog);

        });


        return result;
    }
}
