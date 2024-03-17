package com.example.controller;

import com.example.entity.News;
import com.example.service.NewsService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/*
    * 生成新闻静态页面相关接口
    * */
@RestController
@RequestMapping(value = "/view")
public class ExportController {
    private final NewsService newsService;
    public ExportController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/{id}")
    public ModelAndView getNewsDetail(Model model,@PathVariable("id") Integer id) {
        News news = newsService.getNewsDetail(id);
        model.addAttribute("news",news);
        return new ModelAndView("news_Info");
    }


}
