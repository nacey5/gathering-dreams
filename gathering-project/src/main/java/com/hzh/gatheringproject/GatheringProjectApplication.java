package com.hzh.gatheringproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.hzh.gatheringproject.mapper")
/*@ComponentScan("com.hzh.gatheringproject.service")*/
public class GatheringProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatheringProjectApplication.class, args);
    }

}
