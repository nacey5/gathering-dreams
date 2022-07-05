package com.hzh.gatheringproject.service;

import com.hzh.gatheringproject.dto.LoginFormDTO;
import com.hzh.gatheringproject.dto.RegisterInfoDTO;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author DAHUANG
 * @date 2022/7/5
 */
@Service
public interface UserService {
    /**
     * select User by condition
     * @param loginFormDTO
     * @param password
     * @return
     */
    Result selectUserByCodeAndPassword(LoginFormDTO loginFormDTO, String password);

    /**
     * insert into by condition
     * @param registerInfoDTO
     * @return
     */
    Result insertUserForRegister(RegisterInfoDTO registerInfoDTO);
}
