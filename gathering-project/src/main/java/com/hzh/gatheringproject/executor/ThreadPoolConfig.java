package com.hzh.gatheringproject.executor;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.Setter;

/**
 * 线程池配置类
 *
 * @author DAHUANG
 * @date 2022/7/27
 */
@Setter
@EnableAsync
@Configuration
@ConfigurationProperties(prefix = "task.pool")
public class ThreadPoolConfig {

    /** 线程池中的核心线程数量,默认为1 */
    private int corePoolSize = 5;
    /** 线程池中的最大线程数量 */
    private int maxPoolSize = 10;
    /** 线程池中允许线程的空闲时间,默认为 60s */
    private int keepAliveTime = ((int) TimeUnit.SECONDS.toSeconds(30));
    /** 线程池中的队列最大数量 */
    private int queueCapacity = 1000;

    /** 线程的名称前缀 */
    private static final String THREAD_PREFIX = "thread-call-runner-%d";

    @Bean("asyncTaskExecutor")
    @Lazy
    public ThreadPoolTaskExecutor threadPool(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(THREAD_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
