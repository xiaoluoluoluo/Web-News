package com.example.aop;


import com.alibaba.fastjson.JSONObject;

import com.example.entity.LoginLog;
import com.example.mapper.LoginLogMapper;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@Slf4j
@Component
@Aspect
public class LogAspect {
    private final LoginLogMapper loginLogMapper;

    public LogAspect(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Pointcut("execution(* com.example.controller.LoginController.login(..))")
    public void pt(){}

    @Around("pt()")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        //操作人username
        Object[] args = joinPoint.getArgs();
        String toString = Arrays.toString(args);
        String regex = "username=(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(toString);
        String loginUser = null;
        if (matcher.find()) {
            loginUser = matcher.group(1);
        }

        //操作时间
        LocalDateTime operateTime = LocalDateTime.now();

        //调用原始方法运行
        Object result = joinPoint.proceed();
        //方法返回值
        String returnValue = JSONObject.toJSONString(result);

        LoginLog loginLog=new LoginLog(null,loginUser,returnValue,operateTime);
        loginLogMapper.insert(loginLog);

        log.info("AOP记录操作日志:{}",loginLog);
        return result;

    }
}
