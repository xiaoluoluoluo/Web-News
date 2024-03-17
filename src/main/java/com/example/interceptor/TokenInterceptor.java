package com.example.interceptor;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.UserConstant;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.utils.JedisUtils;
import com.example.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private JedisUtils jedisUtils;
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取验证码，用户登录不拦截与验证
        String requestURL = request.getRequestURL().toString();
        if (requestURL.contains("/login/code")
                || requestURL.contains("/login/check")
                || requestURL.contains("/login/sign")
                || requestURL.contains("/view/")
                || requestURL.contains("/view")
                || requestURL.endsWith(".html")){
            response.setHeader("tokenstatus", "ok");
            return true;
        } else {
            if (request.getHeader("token") == null) {
                response.setHeader("tokenstatus", "no");
                return false;
            } else {
                // 通过客户端传递的Token参数进行验证，注意header中的属性名要小写
                Claims token = JwtUtils.parseJWT(request.getHeader("token"));
                String username = (String) token.get("username");
                QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("username",username);
                User user = userMapper.selectOne(wrapper);
                if (user.getStatus() == UserConstant.FORBID){
                    response.setHeader("tokenstatus", "disable");
                    return false;
                }
                Jedis jedis = jedisUtils.getJedis();
                String value = jedis.get(username);
                if (value!=null) {
                    response.setHeader("tokenstatus", "ok");
                    return true;
                } else {
                    response.setHeader("tokenstatus", "timeout");
                    return false;
                }
            }
        }
    }

}
