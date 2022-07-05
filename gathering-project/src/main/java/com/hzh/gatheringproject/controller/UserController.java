package com.hzh.gatheringproject.controller;

import com.hzh.gatheringproject.dto.LoginFormDTO;
import com.hzh.gatheringproject.dto.RegisterInfoDTO;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.entity.User;
import com.hzh.gatheringproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author DAHUANG
 * @date 2022/7/5
 */
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Result loginTest(@RequestBody LoginFormDTO loginFormDTO, HttpSession session){
        return userService.selectUserByCodeAndPassword(loginFormDTO, loginFormDTO.getPassword());
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterInfoDTO registerInfoDTO){
        return userService.insertUserForRegister(registerInfoDTO);
    }
}
