package com.hzh.gatheringproject;

import com.hzh.gatheringproject.controller.UploadController;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.util.RegexUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class GatheringProjectApplicationTests {

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
    }

}
