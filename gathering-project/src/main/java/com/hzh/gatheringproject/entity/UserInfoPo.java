package com.hzh.gatheringproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author DAHUANG
 * @date 2022/7/19
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_info_po")
public class UserInfoPo {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("user_id")
    private int userId;
    private String company;
    private String position;
    private String introduction;
    @TableField("we_chat")
    private String weChat;
}
