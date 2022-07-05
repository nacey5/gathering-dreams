package com.hzh.gatheringproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hzh.gatheringproject.mapper")
public class GatheringProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatheringProjectApplication.class, args);
    }

}
