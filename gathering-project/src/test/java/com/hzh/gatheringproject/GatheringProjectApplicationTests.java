package com.hzh.gatheringproject;

import com.hzh.gatheringproject.controller.UploadController;
import com.hzh.gatheringproject.util.RegexUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GatheringProjectApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    void testRegex(){
        String phone="13670583984";
        System.out.println(RegexUtils.isPhoneInvalid(phone));
    }

    @Test
    void testDirPath(){
        UploadController uploadController=new UploadController();
        String newFileName = uploadController.createNewFileName("toutou.png");
        System.out.println(newFileName);
    }
}
