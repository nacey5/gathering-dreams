package com.hzh.gatheringproject.dto;

import com.hzh.gatheringproject.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author DAHUANG
 * @date 2022/7/6
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RegisterInfoDTO extends User {
    private String name;
    private String nickName;
    private String phone;
    private int gender;
    private String email;
    private int age;
    private String password;
    private String icon;
    private String phoneCode;
    private String emailCode;
}
