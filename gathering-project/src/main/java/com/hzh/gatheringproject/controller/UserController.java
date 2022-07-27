package com.hzh.gatheringproject.controller;

import com.hzh.gatheringproject.dto.*;
import com.hzh.gatheringproject.service.UserService;
import com.hzh.gatheringproject.util.UserHolder;
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
        return userService.selectUserByCodeAndPassword(loginFormDTO, session);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterInfoDTO registerInfoDTO){
        return userService.insertUserForRegister(registerInfoDTO);
    }

    @PostMapping("/code")
    public Result sendCode(@RequestParam("phone") String phone,HttpSession session){
       return userService.sendCode(phone,session);
    }

    @PostMapping("/emailCode")
    public Result sendMailCode(@RequestParam("email") String email){
        return userService.sendMailCode(email);
    }

    @GetMapping("/me")
    public Result me(){
        UserDTO user = UserHolder.getUser();
        log.warn(user.toString());
        return Result.ok(user);
    }

    @PostMapping("/completePersonal")
    public Result completePersonal(@RequestBody UserComplete userComplete){
        return userService.completePersonal(userComplete);
    }

    @GetMapping("/meDetail")
    public Result meDetail(@RequestParam("id") int id){
        return userService.meDetail(id);
    }


}
