package com.hzh.gatheringproject.service;

import com.hzh.gatheringproject.dto.Result;
import org.springframework.stereotype.Service;

/**
 * @author DAHUANG
 * @date 2022/7/20
 */
@Service
public interface FollowerService{

    /**
     * 关注某人某项目或者取消关注某人
     * @param id 关注的人的id
     * @param type 关注的类型是人还是项目还是讲师
     * @param isFollow 是已经被关注
     * @return
     */
    Result follow(int id, int type, Boolean isFollow);

    /**
     * 是否已经关注
     * @param followUserID
     * @param type
     * @return
     */
    Result isFollow(int followUserID,int type);

    /**
     * 查找我关注的此类型的所有数据
     * @param type
     * @return
     */
    Result myFollowers(int type);
}
