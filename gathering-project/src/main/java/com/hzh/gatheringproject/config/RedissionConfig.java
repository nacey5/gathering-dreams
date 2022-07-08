package com.hzh.gatheringproject.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;

/**
 * @author DAHUANG
 * @date 2022/7/6
 */
public class RedissionConfig {

    @Bean
    public RedissonClient redissonClient(){
        //配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.48.129:6380");

        //创建redisson对象
        return Redisson.create(config);
    }
}
