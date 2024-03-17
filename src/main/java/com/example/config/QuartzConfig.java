package com.example.config;

import com.example.job.PushJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail jobDetail(){
        return JobBuilder.newJob(PushJob.class)
                .storeDurably()
                .withIdentity("job1","group1")
                .usingJobData("count",1) //共享数据
                .build();
    }
    @Bean
    public Trigger trigger(){
        String express = "0/2 * * * * ? *";
        return TriggerBuilder.newTrigger()
                .withIdentity("trigger1","group1")
                .forJob(jobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule(express))
                .build();
    }
}
