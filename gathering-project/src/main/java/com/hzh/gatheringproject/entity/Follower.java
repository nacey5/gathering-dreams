package com.hzh.gatheringproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author DAHUANG
 * @date 2022/7/20
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("t_follow")
public class Follower{

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("be_follower_id")
    private int beFollowerId;
    private int type;
    @TableField("follow_user_id")
    private int followUserId;

}
