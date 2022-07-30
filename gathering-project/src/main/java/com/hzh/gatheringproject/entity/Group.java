package com.hzh.gatheringproject.entity;

import lombok.Data;

import java.util.Collections;
import java.util.Set;

/**
 * @author DAHUANG
 * @date 2022/7/27
 * 聊天室，聊天组
 */
@Data
public class Group {
    // 聊天室名称
    private String name;
    // 聊天室成员
    private Set<String> members;

    public static final Group EMPTY_GROUP = new Group("empty", Collections.emptySet());

    public Group(String name, Set<String> members) {
        this.name = name;
        this.members = members;
    }
}
