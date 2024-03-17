package com.example.exception;

import com.example.entity.Result;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public Result<String> handException(ExpiredJwtException ex){
        System.out.println("token已经过期");
        return Result.error("token已过期，请重新登录");
    }
}
