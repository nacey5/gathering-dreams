package com.hzh.gatheringproject.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author DAHUANG
 * @date 2022/7/29
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class GroupDto {
    String groupName;
    String[] ids;
    int id;
}
