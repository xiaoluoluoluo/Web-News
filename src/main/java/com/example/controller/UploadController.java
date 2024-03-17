package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.example.entity.Result;
import com.example.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

    /*
     * 文件上传功能相关接口
     * */
@Slf4j
@RestController
public class UploadController {

    //阿里云OSS
    private final AliOSSUtils aliOSSUtils;

    public UploadController(AliOSSUtils aliOSSUtils) {
        this.aliOSSUtils = aliOSSUtils;
    }

    @PostMapping("/editor/upload")
    public Dict editorUpload(MultipartFile file) throws IOException {
        log.info("富文本内文件上传：{}",file);
        //调用阿里云 oss工具类
        String url = aliOSSUtils.upload(file);
        log.info("文件上传成功，访问路径：{}",url);
        return Dict.create().set("errno",0).set("data", CollUtil.newArrayList(Dict.create().set("url",url)));
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传:{}",file);
        //调用阿里云 oss工具类
        String url = aliOSSUtils.upload(file);
        log.info("文件上传成功，访问路径：{}",url);
        return Result.success(url);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/upfile")
    public Result<String> upfile(MultipartFile file) throws IOException {
        log.info("文件上传:{}",file);
        //调用阿里云 oss工具类
        String url = aliOSSUtils.upfile(file);
        log.info("文件上传成功，访问路径：{}",url);
        return Result.success(url);
    }
}
