package com.hzh.gatheringproject;

import com.hzh.gatheringproject.util.RegexUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;

@SpringBootTest
class GatheringProjectApplicationTests {

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    void contextLoads() {
    }


    @Test
    void testRegex(){
        String phone="123";
        System.out.println(RegexUtils.isPhoneInvalid(phone));
    }

    @Test
    void testDirPath(){
        if (!RegexUtils.isEmailInvalid("1160124555@qq.com")&&!RegexUtils.isPhoneInvalid("12345678904")&&!RegexUtils.isPasswordInvalid("poslmzqnx")) {
            System.out.println(true);
        }
        else {
            System.out.println(false);
        }
    }

    @Test
    void testRegister(){
        System.out.println(UUID.randomUUID());
    }

    @Test
    void testNettyClient(){
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <100 ; i++) {
                    if (i%2==0){
                        System.out.println(i);
                    }
                }
            }
        });
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <100 ; i++) {
                    if (i%2==1){
                        System.out.println(i);
                    }
                }
            }
        });

        threadPoolTaskExecutor.shutdown();

    }

}
