package com.hzh.gatheringproject.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author DAHUANG
 * @date 2022/7/6
 */
@Data
public class LoginFormDTO implements Serializable {
    String codePhoneOrEmail;
    String password;
    String checkCode;
}
