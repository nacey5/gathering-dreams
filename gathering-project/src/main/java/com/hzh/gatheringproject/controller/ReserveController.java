package com.hzh.gatheringproject.controller;

import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.service.ReserveService;
import com.hzh.gatheringproject.util.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author DAHUANG
 * @date 2022/7/31
 */
@RestController
@RequestMapping("/reserve")
public class ReserveController {

    @Autowired
    private ReserveService reserveService;

    @PostMapping("/getMyUserInfo")
    public Result getMyInfo(){
        return reserveService.getMyUserInfo();
    }

    @PostMapping("/getMySystemInfo")
    public Result getSystemInfo(){
        return reserveService.getSystemInfo();
    }


    @PostMapping("/sendReserveInfo")
    public Result sendReserveInfo(@RequestParam("toId") int id,@RequestParam("content")String content){
        return reserveService.sendReserveInfo(id,content);
    }

}
