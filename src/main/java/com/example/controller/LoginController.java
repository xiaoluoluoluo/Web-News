package com.example.controller;


import com.example.dto.KaptchaDTO;
import com.example.dto.UserLoginDTO;
import com.example.dto.UserSignDTO;
import com.example.entity.Result;
import com.example.service.LoginService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

/*
 * 用户登录页面相关接口
 * */
@RestController
@RequestMapping(value = "/login")
@Slf4j
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/code")
    public Result<KaptchaDTO> getCode() {
        KaptchaDTO kaptchaDTO;
        try {
            kaptchaDTO = loginService.getCode();
            log.info("生成验证码：{}", kaptchaDTO);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return Result.success(kaptchaDTO);
    }

    @PostMapping("/check")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录验证：{}", userLoginDTO);
        if (userLoginDTO.isChecked()){
            System.out.println("含有cookie");
        }
        return loginService.login(userLoginDTO);
    }

    @PostMapping("/sign")
    public Result<String> sign(@RequestBody UserSignDTO userSignDTO){
        log.info("用户注册功能：{}",userSignDTO);
        return loginService.sign(userSignDTO);
    }

}
