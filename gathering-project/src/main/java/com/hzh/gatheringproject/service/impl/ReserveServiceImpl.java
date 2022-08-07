package com.hzh.gatheringproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.entity.ReserveInfo;
import com.hzh.gatheringproject.exception.ReserveException;
import com.hzh.gatheringproject.mapper.ReserveMapper;
import com.hzh.gatheringproject.service.ReserveService;
import com.hzh.gatheringproject.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author DAHUANG
 * @date 2022/7/31
 */
@Slf4j
@Service
public class ReserveServiceImpl implements ReserveService {

    @Resource
    private ReserveMapper reserveMapper;

    @Override
    public Result getMyUserInfo() {
        int id= UserHolder.getUser().getId();
        QueryWrapper<ReserveInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_id",id).eq("type",0);
        List<ReserveInfo> reserveInfos = reserveMapper.selectList(queryWrapper);
        return Result.ok(reserveInfos, (long) reserveInfos.size());
    }

    @Override
    public Result getSystemInfo() {
        int id= UserHolder.getUser().getId();
        QueryWrapper<ReserveInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_id",id).eq("type",1);
        List<ReserveInfo> reserveInfos = reserveMapper.selectList(queryWrapper);
        return Result.ok(reserveInfos, (long) reserveInfos.size());
    }

    @Override
    @Transactional
    public Result sendReserveInfo(int id, String content) {
        int from=UserHolder.getUser().getId();
        ReserveInfo reserveInfo = new ReserveInfo();
        reserveInfo.setContent(content);
        reserveInfo.setSendId(from);
        reserveInfo.setToId(id);
        int insert = reserveMapper.insert(reserveInfo);
        if (ReserveException.sendReserveResult(insert)) {
            return Result.ok("发送成功");
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        log.warn("发送失败");
        return Result.fail("发送失败，未知错误");
    }


}
