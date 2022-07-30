package com.hzh.gatheringproject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * 线程池配置测试
 *
 * @author
 * @date
 */
@SpringBootTest
public class ThreadPoolConfigTest {

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Test
    public void testThreadPoolTaskExecutor() {

        LongAdder adder = new LongAdder();

        List<ListenableFuture<?>> futures = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            // 注意这里
            // 这里提交的是无返回值的任务，有返回值的任务需要实现 Callable 接口
            // futures 用于收集线程的监听结果，由于是异步的，主线程是不会等待线程池里面的任务的
            futures.add(executor.submitListenable(new AutomaticIncTask(adder)));
        }

        // 附带阻塞主线程效果，目的是为了让主线程等待线程池中的线程执行完毕
        futures.forEach(ListenableFuture::completable);

        System.out.println("执行完毕:" + adder.longValue());

    }

}
