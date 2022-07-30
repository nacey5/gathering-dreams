package com.hzh.gatheringproject.typemap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.hzh.gatheringproject.generics.FollowerConstants.*;
import static com.hzh.gatheringproject.generics.RedisConstants.*;

/**
 * @author DAHUANG
 * @date 2022/7/26
 */
@Component
public class ReflectMap {
    public static final Map<Integer,String> followTypeMap=new HashMap<>();
    static {
        followTypeMap.put(FOLLOWER_USER,FOLLOW_TYPE_USER);
        followTypeMap.put(FOLLOWER_PROJECT,FOLLOW_TYPE_PROJECT);
        followTypeMap.put(FOLLOWER_TEACHER,FOLLOW_TYPE_TEACHER);
        followTypeMap.put(FOLLOWER_COURSE,FOLLOW_TYPE_COURSE);
    }
}
