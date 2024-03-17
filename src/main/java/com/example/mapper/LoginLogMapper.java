package com.example.mapper;

import com.example.entity.LoginLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper {

    @Insert("insert into login_log (login_user, return_value, operate_time) VALUES (#{loginUser},#{returnValue},#{operateTime})")
    void insert(LoginLog loginLog);
}
