package com.example.job;

import com.example.service.NewsService;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PushJob implements Job {
    @Autowired
    private NewsService newsService;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            newsService.generator();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JobDetail jobDetail = context.getJobDetail();
        System.out.println("任务名字"+jobDetail.getKey().getName());
        System.out.println("推送成功");
    }
}
