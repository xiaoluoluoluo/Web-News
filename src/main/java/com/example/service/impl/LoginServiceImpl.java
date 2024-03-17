package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.constant.UserConstant;
import com.example.dto.KaptchaDTO;
import com.example.dto.UserLoginDTO;
import com.example.dto.UserSignDTO;
import com.example.entity.Result;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.LoginService;
import com.example.utils.JedisUtils;
import com.example.utils.JwtUtils;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private JedisUtils jedisUtils;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HttpServletResponse response;
    @Override
    public KaptchaDTO getCode() throws IOException {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage bufferedImage = kaptchaProducer.createImage(text);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String base64 = base64Encoder.encode(outputStream.toByteArray());
        String captchaBase64 = base64.replaceAll("\r\n", "");
        // 存入Redis数据库
        String randomId = RandomStringUtils.random(15, true, true);
        Jedis jedis = jedisUtils.getJedis();
        jedis.set(randomId, text);
        jedis.expire(randomId,60);
        // 生成验证码返回数据
        KaptchaDTO kaptchaDTO = new KaptchaDTO();
        kaptchaDTO.setUuid(randomId);
        kaptchaDTO.setBase64(captchaBase64);
        return kaptchaDTO;
    }

    @Override
    public Result<String> login(UserLoginDTO userLoginDTO) {

        Jedis jedis = jedisUtils.getJedis();

        //判断验证码信息
        if (jedis.get(userLoginDTO.getUuid())==null){
            return Result.error("验证码已过期,请单击刷新");
        }
        if(!userLoginDTO.getImageCode().equals(jedis.get(userLoginDTO.getUuid()))){
            return Result.error("验证码错误");
        }

        //判断用户信息
/*        User user=userMapper.getUser(userLoginDTO.getUsername());*/
        QueryWrapper<User> wrapper=new QueryWrapper<User>()
                .eq("username",userLoginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user==null){
            return Result.error("用户名不存在");
        }else{

            if (user.getStatus() == UserConstant.FORBID){
                return Result.error("该账号已被禁用，请联系管理员");
            }

            //判断用户是否处于冻结
            //1.没有冻结
            //2.冻结
            if(user.getStatus() != UserConstant.NORMAL){
                if(user.getFreezeTime().isAfter(LocalDateTime.now())){
                    //返回剩余冻结时间
                    Duration duration = Duration.between(user.getFreezeTime(), LocalDateTime.now());
                    long minutes = duration.abs().toMinutes();
                    return Result.error("账号已被冻结，请"+minutes+"分钟后重试");
                }else{
                    user.setError(0);
                    user.setStatus(UserConstant.NORMAL);
                    user.setFreezeTime(null);
                    UpdateWrapper<User> wrapper1 =new UpdateWrapper<User>().eq("username",user.getUsername());
                    userMapper.update(user,wrapper1);
                }
            }

            //密码错误
            if (!user.getPassword().equals(userLoginDTO.getPassword())){
                Integer error = user.getError();
                System.out.println(error);
                error=error+1;
                user.setError(error);
                if(user.getError()>2){
                    user.setStatus(UserConstant.FREEZING);
                    user.setFreezeTime(LocalDateTime.now().plusMinutes(15));
                }
                UpdateWrapper<User> wrapper1 =new UpdateWrapper<User>().eq("username",user.getUsername());
                userMapper.update(user,wrapper1);

                return Result.error("密码错误");
            }

            //登录成功，将用户登录错误次数改为0
            user.setError(0);
            user.setFreezeTime(null);
            UpdateWrapper<User> wrapper1 =new UpdateWrapper<User>().eq("username",user.getUsername());
            userMapper.update(user,wrapper1);

            Map<String, Object> claims=new HashMap<>();
            claims.put("id",user.getId());
            claims.put("username",user.getUsername());
            String jwt = JwtUtils.generateJwt(claims);
            //将JWT存入Redis中
            jedis.set(user.getUsername(),jwt);
            jedis.expire(user.getUsername(), 12*60*60);

            return Result.success(jwt);
        }
    }

    @Override
    public Result<String> sign(UserSignDTO userSignDTO) {

        //判断用户名是否存在
        QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("username",userSignDTO.getUsername());
        User checkuser=userMapper.selectOne(wrapper);

        if (checkuser!=null){
            return Result.error("用户名已存在");
        }

        User user=new User();
        user.setUsername(userSignDTO.getUsername());
        user.setPassword(userSignDTO.getPassword());
        user.setImage("https://luo-web.oss-cn-hangzhou.aliyuncs.com/7a993ee4-dd90-489f-8c4e-317b271fbcc0.jpg");
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return Result.success("注册成功");
    }
}
