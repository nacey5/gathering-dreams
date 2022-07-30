package com.hzh.gatheringproject;

import java.util.concurrent.atomic.LongAdder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 原子自增任务
 *
 * @author
 * @date
 */
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AutomaticIncTask implements Runnable {

    private LongAdder adder;

    @Override
    public void run() {

        for (int i = 0; i < 1000; i++) {
            adder.increment();
        }

    }

}
