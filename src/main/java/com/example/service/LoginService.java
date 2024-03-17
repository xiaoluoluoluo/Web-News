package com.example.service;

import com.example.dto.KaptchaDTO;
import com.example.dto.UserLoginDTO;
import com.example.dto.UserSignDTO;
import com.example.entity.Result;

import java.io.IOException;

public interface LoginService {
    KaptchaDTO getCode() throws IOException;

    Result<String> login(UserLoginDTO userLoginDTO);

    Result<String> sign(UserSignDTO userSignDTO);
}
