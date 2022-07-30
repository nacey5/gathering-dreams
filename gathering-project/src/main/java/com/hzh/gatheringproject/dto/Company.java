package com.hzh.gatheringproject.dto;

import com.hzh.gatheringproject.entity.UserInfoPo;
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
public class Company extends UserInfoPo {
    int userId;
    String company;
    String position;
    String introduction;
    String weChat;
}
