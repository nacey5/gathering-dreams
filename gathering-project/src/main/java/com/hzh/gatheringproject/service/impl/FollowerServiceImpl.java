package com.hzh.gatheringproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.entity.Follower;
import com.hzh.gatheringproject.exception.RegisterException;
import com.hzh.gatheringproject.generics.RedisConstants;
import com.hzh.gatheringproject.mapper.FollowMapper;
import com.hzh.gatheringproject.service.FollowerService;
import com.hzh.gatheringproject.typemap.ReflectMap;
import com.hzh.gatheringproject.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;

import java.util.List;

import static com.hzh.gatheringproject.generics.FollowerConstants.*;
import static com.hzh.gatheringproject.typemap.ReflectMap.followTypeMap;

/**
 * @author DAHUANG
 * @date 2022/7/20
 */
@Slf4j
@Service
public class FollowerServiceImpl extends ServiceImpl<FollowMapper,Follower> implements FollowerService {


    @Resource
    private FollowMapper followMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public Result follow(int id, int type, Boolean isFollow) {
        if (!followTypeMap.containsKey(type)){
            return Result.fail("关注类型错误");
        }
        Result follow = isFollow(id,type);
        //在redis中判断是否已经关注
        int userId=UserHolder.getUser().getId();
        if (isFollow){
            if (follow.getPrepare()) {
                stringRedisTemplate.opsForSet().add(RedisConstants.FOLLOW_USER_KEY+followTypeMap.get(type)+userId,id+"");
                return Result.fail("对不起，您已经关注了");
            }
            //往数据库种添加数据，并且保存在redis中
            Follower follower=new Follower();
            follower.setFollowUserId(userId);
            follower.setBeFollowerId(id);
            follower.setType(type);
            if (!RegisterException.followResult(followMapper.insert(follower))) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.fail("关注失败,服务器繁忙");
            }else {
                stringRedisTemplate.opsForSet().add(RedisConstants.FOLLOW_USER_KEY+followTypeMap.get(type)+userId,id+"");
                return Result.ok("关注成功");
            }
        }else {
            //在redis中的数据修改:取关
            QueryWrapper<Follower> wrapper=new QueryWrapper<>();
            wrapper.eq("be_follower_id",id).eq("follow_user_id",userId);
            if (!RegisterException.followResult(followMapper.delete(wrapper))) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.fail("取关失败,您可能还未关注，所以无法取关哦");
            }else {
                //对取关类型进行进行判断
                stringRedisTemplate.opsForSet().remove(RedisConstants.FOLLOW_USER_KEY+followTypeMap.get(type)+userId,id+"");
                return Result.ok("取关成功");
            }

        }
    }

    @Override
    public Result isFollow(int followUserID,int type) {
        int userId=UserHolder.getUser().getId();
        log.warn(followTypeMap.get(type));
        Boolean result = stringRedisTemplate.opsForSet().isMember(RedisConstants.FOLLOW_USER_KEY + followTypeMap.get(type) + userId, followUserID+"");
        if (result==null) {
            QueryWrapper<Follower> wrapper=new QueryWrapper<>();
            wrapper.eq("be_follower_id",followUserID).eq("follow_user_id",userId).eq("type",type);
            //查询是否关注 select count(*) from tb_follow where user_id=? and follow_user_id=?
            Integer count = followMapper.selectCount(wrapper);
            if (count > 0) {
                //有关注
                stringRedisTemplate.opsForSet().add(RedisConstants.FOLLOW_USER_KEY+followTypeMap.get(type)+userId,followUserID+"");
                return Result.ok("您已关注",true);
            }else {
                //没关注
                return Result.ok("您没有关注",false);
            }
        }else if (!result){
            return Result.ok("您没有关注",false);
        }else if (result){
            return Result.ok("您已关注",true);
        }
        return Result.fail("发生未知错误");
    }

    @Override
    public Result myFollowers(int type) {
        int userId=UserHolder.getUser().getId();
        QueryWrapper<Follower> wrapper=new QueryWrapper<>();
        wrapper.eq("follow_user_id",userId).eq("type",type);
        List<Follower> followers = followMapper.selectList(wrapper);
        if (followers.isEmpty()||followers.size()==0){
            return Result.fail("未查到数据");
        }
        return Result.ok(followers, (long) followers.size());
    }
}
