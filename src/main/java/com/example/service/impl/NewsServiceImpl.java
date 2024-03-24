package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.NewsConstant;
import com.example.dto.NewsDto;
import com.example.dto.TableReqDTO;
import com.example.dto.TableRspDTO;
import com.example.entity.News;
import com.example.entity.Result;
import com.example.mapper.NewsMapper;
import com.example.service.NewsService;
import com.example.utils.RedissonUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsMapper newsMapper;

    @Override
    public Result<String> addNews(NewsDto newsDto) {

        News news=new News();
        BeanUtils.copyProperties(newsDto,news);
        news.setStatus(NewsConstant.UN_PUBLISH);
        news.setOperateTime(LocalDateTime.now());
        if(newsDto.getId()==null){
            newsMapper.insert(news);
            return Result.success("提交成功");
        }else{
            RLock lock = RedissonUtils.getLock("update:" + newsDto.getId());
            try {
                if(lock.tryLock(20, TimeUnit.SECONDS)){
                    newsMapper.updateById(news);
                    return Result.success("修改成功");
                }else{
                    return Result.error("正在被修改");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                lock.unlock();
            }

        }
    }
    @Override
    public TableRspDTO listPublished(TableReqDTO tableReqDTO) {
        IPage page = new Page(tableReqDTO.getCurrentPage(), tableReqDTO.getPageSize());
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<News>()
                .like(News::getTitle,tableReqDTO.getQueryText())
                .eq(News::getStatus,NewsConstant.PUBLISH)
                .orderByDesc(News::getOperateTime);
        newsMapper.selectPage(page,wrapper);
        return new TableRspDTO(page.getTotal(),page.getRecords());
    }

    @Override
    public Result<News> newsInfo(Integer id) {
        News news = newsMapper.selectById(id);
        return Result.success(news);
    }

    @Override
    public List<News> getPublished() {
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        return newsMapper.selectList(queryWrapper);
    }

    @Override
    public News getNewsDetail(Integer id) {
        return newsMapper.selectById(id);
    }

    @Override
    public TableRspDTO listTable(TableReqDTO tableReqDTO) {
        IPage page = new Page(tableReqDTO.getCurrentPage(), tableReqDTO.getPageSize());
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<News>()
                .like(News::getTitle,tableReqDTO.getQueryText())
                .orderByDesc(News::getOperateTime);
        newsMapper.selectPage(page,wrapper);
        return new TableRspDTO(page.getTotal(),page.getRecords());
    }

    @Override
    public Result<String> publish(Integer id) {
        News news = newsMapper.selectById(id);
        news.setStatus(NewsConstant.PUBLISH);
        news.setPublishTime(LocalDateTime.now());
        news.setOperateTime(LocalDateTime.now());
        UpdateWrapper<News> wrapper = new UpdateWrapper<News>().eq("id",id);
        newsMapper.update(news,wrapper);
        return Result.success("发布成功");
    }

    @Override
    public Result<String> newsDelete(Integer id) {
        newsMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result<String> newsContext(Integer id) {
        News news = newsMapper.selectById(id);
        return Result.success(news.getContext());
    }

    @Override
    public Result<String> generator() throws IOException {
        // 创建模板解析器
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");

        // 创建模板引擎
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        // 创建上下文对象
        Context context = new Context();
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<News>().eq(News::getStatus,NewsConstant.PUBLISH);
        List<News> newsList = newsMapper.selectList(wrapper);
        context.setVariable("newsList", newsList);

        //文件输出的路径及文件名
        FileWriter writer = new FileWriter("src/main/resources/static/views.html");

        // 生成HTML代码，参数：模板，数据，文件输出流
        engine.process("index", context, writer);
        //关闭文件
        writer.close();

        return Result.success("页面成功生成");
    }
}

