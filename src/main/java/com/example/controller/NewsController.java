package com.example.controller;

import com.example.dto.NewsDto;
import com.example.dto.TableReqDTO;
import com.example.dto.TableRspDTO;
import com.example.entity.News;
import com.example.entity.Result;
import com.example.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/*
     * 新闻管理页面相关接口
     * */
@Slf4j
@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /*
    * 如何id为null，则新增新闻
    * 如果id不为null，则修改新闻
    * */
    @PostMapping
    public Result<String> addNews(@RequestBody NewsDto newsDto){
        log.info("新增新闻或修改新闻：{}",newsDto.getId());
        return newsService.addNews(newsDto);
    }

    @PostMapping("/list")
    public Result<TableRspDTO> newsList(@RequestBody TableReqDTO tableReqDTO){
        log.info("获取新闻管理列表");
        TableRspDTO tableResponseDTO = newsService.listTable(tableReqDTO);
        return Result.success(tableResponseDTO);
    }

    @PostMapping("/published")
    public Result<TableRspDTO> newsListPublished(@RequestBody TableReqDTO tableReqDTO){
        log.info("获取已发布的新闻列表");
        TableRspDTO tableRspDTO = newsService.listPublished(tableReqDTO);
        return Result.success(tableRspDTO);
    }

    @GetMapping("/publish")
    public Result<String> newsPublish(@RequestParam("queryParam") Integer id){
        log.info("将要发布此id的新闻：{}",id);
        return newsService.publish(id);
    }

    @DeleteMapping
    public Result<String> newsDelete(@RequestParam("queryParam") Integer id){
        log.info("将要删除此id的新闻：{}",id);
        return newsService.newsDelete(id);
    }

    @GetMapping
    public Result<String> newsContext(@RequestParam("queryParam") Integer id){
        log.info("获取此id的新闻内容：{}",id);
        return newsService.newsContext(id);
    }

    @GetMapping("/info")
    public Result<News> newsInfo(@RequestParam("queryParam") Integer id){
        log.info("获取此id的新闻的全部信息");
        return newsService.newsInfo(id);
    }
    @GetMapping("/generator")
    public Result<String> generator() throws IOException {
        log.info("生成一级静态页面");
        return newsService.generator();
    }
}
