package com.example.controller;

import com.example.dto.UserDTO;
import com.example.entity.Result;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

    /*
     * 用户管理模块相关接口
     * */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public Result<List<User>> list(){
        log.info("获取用户列表信息");
        return userService.userList();
    }

    @GetMapping("/info")
    public Result<UserVO> info( String queryParam){
        log.info("获取单个用户名信息：{}",queryParam);
        return userService.getInfo(queryParam);
    }

    @PutMapping("/status")
    public Result<String> updateStatus(@RequestParam("queryParam") Integer id){
        log.info("修改此id的用户状态:{}",id);
        return userService.updateStatus(id);
    }
    @PostMapping("/update")
    public Result<String> updateInfo(@RequestBody UserDTO userDTO){
        log.info("修改用户信息:{}",userDTO);
        return userService.updateInfo(userDTO);
    }

    //导出用户信息
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        log.info("将用户信息导出");
        userService.export(response);
    }
}
