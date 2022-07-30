package com.hzh.gatheringproject.session.factory;

import com.hzh.gatheringproject.session.service.GroupSession;
import com.hzh.gatheringproject.session.service.impl.GroupSessionMemoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public abstract class GroupSessionFactory {

    @Autowired
    private static GroupSession groupSession=new GroupSessionMemoryImpl();

    @Bean
    public static GroupSession getGroupSession() {
        return groupSession;
    }
}
