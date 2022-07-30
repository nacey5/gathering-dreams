package com.hzh.gatheringproject.controller;

import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.service.FollowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author DAHUANG
 * @date 2022/7/20
 */

@Slf4j
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    FollowerService followerService;

    @GetMapping("/followOrUnFollow")
    public Result follow(@RequestParam("id") int id,@RequestParam("type") int type,@RequestParam("isFollow") Boolean isFollow){
       return followerService.follow(id,type,isFollow);
    }

    @GetMapping("/or/not")
    public Result isFollow(@RequestParam("id") int followUserID,@RequestParam("type") int type){
        return followerService.isFollow(followUserID,type);
    }

    @GetMapping("/myFollowers")
    public Result myFollowers(@RequestParam("type") int type){
        return followerService.myFollowers(type);
    }

}
