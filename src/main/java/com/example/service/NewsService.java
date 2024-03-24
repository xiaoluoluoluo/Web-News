package com.example.service;

import com.example.dto.NewsDto;
import com.example.dto.TableReqDTO;
import com.example.dto.TableRspDTO;
import com.example.entity.News;
import com.example.entity.Result;

import java.io.IOException;
import java.util.List;


public interface NewsService {
    Result<String> addNews(NewsDto newsDto) throws InterruptedException;

    Result<String> publish(Integer id);

    Result<String> newsDelete(Integer id);

    Result<String> newsContext(Integer id);

    TableRspDTO listPublished(TableReqDTO tableReqDTO);

    Result<News> newsInfo(Integer id);

    List<News> getPublished();

    News getNewsDetail(Integer id);

    TableRspDTO listTable(TableReqDTO tableReqDTO);

    Result<String> generator() throws IOException;

}
