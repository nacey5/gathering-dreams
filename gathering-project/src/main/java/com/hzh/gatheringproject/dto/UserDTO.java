package com.hzh.gatheringproject.dto;

import com.hzh.gatheringproject.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author DAHUANG
 * @date 2022/7/11
 * 可以暴漏的用户信息
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UserDTO extends User {
    private  int id;
    private  String name;
    private  String nickName;
    private  String phone;
    private  int gender;
    private  String email;
    private  int age;
    private  String icon;
    private LocalDateTime createTime;
    private  LocalDateTime updateTime;
}
