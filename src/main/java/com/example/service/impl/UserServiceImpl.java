package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.constant.UserConstant;
import com.example.dto.UserDTO;
import com.example.entity.Result;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.example.vo.UserVO;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Result<UserVO> getInfo(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("username",username);
        User user = userMapper.selectOne(wrapper);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return Result.success(userVO);
    }

    @Override
    public Result<List<User>> userList() {
        List<User> users = userMapper.selectList(null);
        return Result.success(users);
    }

    @Override
    public Result<String> updateStatus(Integer id) {
        User user = userMapper.selectById(id);
        if(Objects.equals(user.getStatus(), UserConstant.FORBID)){
            user.setStatus(UserConstant.NORMAL);
            user.setError(0);
            user.setFreezeTime(null);
        }else{
            user.setStatus(UserConstant.FORBID);
        }
        UpdateWrapper<User> wrapper1 =new UpdateWrapper<User>().eq("username",user.getUsername());
        userMapper.update(user,wrapper1);
        return Result.success("用户状态修改成功");
    }

    @Override
    public Result<String> updateInfo(UserDTO userDTO) {
        //如果用户名已经存在
        QueryWrapper<User> wrapper=new QueryWrapper<User>().eq("username",userDTO.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user!=null && !Objects.equals(user.getId(), userDTO.getId())){
            return Result.error("用户名已存在");
        }else{
            User user1 = new User();
            if (!userDTO.getPassword().equals("")) {
                BeanUtils.copyProperties(userDTO, user1);
            } else {
                // 复制除密码字段外的其他属性
                BeanUtils.copyProperties(userDTO, user1, "password");
            }
            userMapper.updateById(user1);
            return Result.success("修改成功");
        }
    }

    @Override
    public void export(HttpServletResponse response) {
        //查询概览数据
        List<User> users = userMapper.selectList(null);

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/UserListInfo.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            assert in != null;
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                String status;
                if(user.getStatus()==1){
                    status="已启用";
                } else if (user.getStatus()==0) {
                    status="已冻结";
                }else{
                    status="已禁用";
                }
                XSSFRow row = sheet.getRow(i+3);
                row.getCell(1).setCellValue(user.getId());
                row.getCell(2).setCellValue(user.getUsername());
                row.getCell(3).setCellValue(user.getPhone());
                row.getCell(4).setCellValue(user.getEmail());
                row.getCell(5).setCellValue(status);
                row.getCell(6).setCellValue(String.valueOf(user.getCreateTime()));
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

