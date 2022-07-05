package com.hzh.gatheringproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzh.gatheringproject.dto.LoginFormDTO;
import com.hzh.gatheringproject.dto.RegisterInfoDTO;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.entity.User;
import com.hzh.gatheringproject.service.UserService;
import com.hzh.gatheringproject.util.RegexUtils;
import org.springframework.stereotype.Service;
import com.hzh.gatheringproject.mapper.*;

import javax.annotation.Resource;

/**
 * @author DAHUANG
 * @date 2022/7/5
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{

    @Resource
    UserMapper userMapper;


    @Override
    public Result selectUserByCodeAndPassword(LoginFormDTO loginFormDTO, String password) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        //对账号格式进行检验，是否为邮箱或者为手机号码
        if (!RegexUtils.isEmailInvalid(loginFormDTO.getCodePhoneOrEmail())||!RegexUtils.isPhoneInvalid(loginFormDTO.getCodePhoneOrEmail())) {
            queryWrapper.eq("phone",loginFormDTO.getCodePhoneOrEmail()).or().eq("email",loginFormDTO.getCodePhoneOrEmail()).eq("password",password);
            User user = userMapper.selectOne(queryWrapper);
            if (user != null&&user.getId()>0) {
                return Result.ok(user);
            }else {
                return Result.fail("密码和手机或邮箱不匹配");
            }
        }
        return Result.fail("手机或者邮箱格式错误");

    }

    @Override
    public Result insertUserForRegister(RegisterInfoDTO registerInfoDTO) {
        //对手机号码和邮箱进行进一步检验
        if (!RegexUtils.isEmailInvalid(registerInfoDTO.getEmail())&&!RegexUtils.isPhoneInvalid(registerInfoDTO.getPhone())&&!RegexUtils.isPasswordInvalid(registerInfoDTO.getPassword())) {
            return Result.fail("您的密码或者手机或者邮箱的格式错误");
        }
        int insert = userMapper.insert(registerInfoDTO);
        if (insert>0) {
            return Result.ok();
        }
        return Result.fail("错误的注册信息");
    }
}
