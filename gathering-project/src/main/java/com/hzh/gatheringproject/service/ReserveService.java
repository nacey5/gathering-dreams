package com.hzh.gatheringproject.service;

import com.hzh.gatheringproject.dto.Result;
import org.springframework.stereotype.Service;

/**
 * @author DAHUANG
 * @date 2022/7/31
 */
@Service
public interface ReserveService {
    /**
     * 根据当前用户的id前往数据库得到信息
     * @return
     */
    Result getMyUserInfo();

    /**
     * 得到用户信息
     * @return
     */
    Result getSystemInfo();

    /**
     * 向id方发送数据
     * @param id
     * @param content
     * @return
     */
    Result sendReserveInfo(int id, String content);
}
