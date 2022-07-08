package com.hzh.gatheringproject.service;

import com.hzh.gatheringproject.dto.LoginFormDTO;
import com.hzh.gatheringproject.dto.RegisterInfoDTO;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.entity.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * @author DAHUANG
 * @date 2022/7/5
 */
@Service
public interface UserService {
    /**
     * select User by condition
     * @param loginFormDTO
     * @param session
     * @return
     */
    Result selectUserByCodeAndPassword(LoginFormDTO loginFormDTO, HttpSession session);

    /**
     * insert into by condition
     * @param registerInfoDTO
     * @return
     */
    Result insertUserForRegister(RegisterInfoDTO registerInfoDTO);

    /**
     * 发送验证码
     * @param phone
     * @param session
     * @return
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * 发送邮箱验证码
     * @param mail
     * @return
     */
    Result sendMailCode(String mail);
}
