package com.hzh.gatheringproject.dto;

import com.hzh.gatheringproject.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author DAHUANG
 * @date 2022/7/19
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UserComplete extends User {
    private UserDTO userDTO;
    private Company company;
}
