package com.example.service;



import com.example.dto.UserDTO;
import com.example.entity.Result;
import com.example.entity.User;
import com.example.vo.UserVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {

    Result<UserVO> getInfo(String username);

    Result<List<User>> userList();

    Result<String> updateStatus(Integer id);

    Result<String> updateInfo(UserDTO userDTO);

    void export(HttpServletResponse response);
}
