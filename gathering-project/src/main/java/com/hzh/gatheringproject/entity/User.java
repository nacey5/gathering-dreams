package com.hzh.gatheringproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author DAHUANG
 * @date 2022/7/5
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private  int id;
    private  String name;
    private  String nickName;
    private  String phone;
    private  String gender;
    private  String email;
    private  int age;
    private  String password;
    private  String icon="";
    private  LocalDateTime createTime;
    private  LocalDateTime updateTime;
}
