package com.hzh.gatheringproject.session.factory;

import com.hzh.gatheringproject.session.service.Session;
import com.hzh.gatheringproject.session.service.impl.SessionMemoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

public abstract class SessionFactory {

    @Resource
    private static Session session=new SessionMemoryImpl();

    @Bean
    public static Session getSession() {
        return session;
    }
}
