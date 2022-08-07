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
 * @date 2022/7/31
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("t_reserve_info")
public class ReserveInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    @TableField("send_id")
    private int sendId;
    @TableField("to_id")
    private int toId;
    private String content;
    private int type;
}
