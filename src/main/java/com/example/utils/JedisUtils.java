package com.example.utils;


import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisUtils {
    public Jedis getJedis(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(50);
        jedisPoolConfig.setMaxIdle(50);
        String host="127.0.0.1";
        int port=6379;
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port);
        return jedisPool.getResource();
    }
}
