package com.example.utils;


import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonUtils {
    public static RedissonClient client;
    static {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1");
        client= Redisson.create(config);
    }
    public static RLock getLock(String key){
        return client.getLock(key);
    }
}
