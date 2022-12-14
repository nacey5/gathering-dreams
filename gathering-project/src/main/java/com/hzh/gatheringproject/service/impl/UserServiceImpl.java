package com.hzh.gatheringproject.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzh.gatheringproject.controller.ClientController;
import com.hzh.gatheringproject.controller.UserController;
import com.hzh.gatheringproject.dto.*;
import com.hzh.gatheringproject.entity.User;
import com.hzh.gatheringproject.entity.UserInfoPo;
import com.hzh.gatheringproject.exception.RegisterException;
import com.hzh.gatheringproject.generics.EmailConstants;
import com.hzh.gatheringproject.generics.RedisConstants;
import com.hzh.gatheringproject.generics.SystemConstants;
import com.hzh.gatheringproject.service.UserService;
import com.hzh.gatheringproject.util.RegexUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.hzh.gatheringproject.mapper.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author DAHUANG
 * @date 2022/7/5
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{

    @Resource
    private UserMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private UserInfoPoMapper userInfoPoMapper;
    @Autowired
    ClientController clientController;
    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Override
    public Result selectUserByCodeAndPassword(LoginFormDTO loginFormDTO, HttpSession session) {

        if (loginFormDTO.getCheckCode()!=null&&!"".equals(loginFormDTO.getCheckCode())){
            List<User> users = null;
            //???????????????????????????
            String checkCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY+loginFormDTO.getCodePhoneOrEmail());
            log.warn(checkCode);
            //????????????????????????sql??????????????????
            if (loginFormDTO.getCheckCode().equals(checkCode)) {
                Map<String,Object> map=new HashMap<>();
                map.put(SystemConstants.CHECK_PHONE,loginFormDTO.getCodePhoneOrEmail());
                users = userMapper.selectByMap(map);
            }
            //??????????????????redis???-???token
            if (users!=null&&users.size()>0) {
                User user = users.get(0);
                String token = UUID.randomUUID().toString(true);
                //??????????????????????????????long???????????????string?????????-java.base/java.lang.Long cannot be cast to java.base/java.lang.String
                Map<String, Object> userMap = BeanUtil.beanToMap(user,new HashMap<>(),
                        CopyOptions.create().setIgnoreNullValue(true).
                                setFieldValueEditor((fieldName,fieldValue)->fieldValue.toString()));
                //t3--???????????????redis???
                stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY+token,userMap);
                //??????token????????????
                stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token, RedisConstants.LOGIN_USER_TTL,TimeUnit.SECONDS);

                clientController.connectToClient(user.getId()+"");
                //??????token
                return Result.ok(token);
            }
            //??????????????????????????????????????????
            return Result.fail("???????????????????????????????????????");
        }
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        //??????????????????????????????????????????????????????????????????
        if (!RegexUtils.isEmailInvalid(loginFormDTO.getCodePhoneOrEmail())||!RegexUtils.isPhoneInvalid(loginFormDTO.getCodePhoneOrEmail())) {
            queryWrapper.eq("phone",loginFormDTO.getCodePhoneOrEmail()).or().eq("email",loginFormDTO.getCodePhoneOrEmail()).eq("password",loginFormDTO.getPassword());
            User user = userMapper.selectOne(queryWrapper);
            if (user != null&&user.getId()>0) {
                //?????????redis???->token??????
                String token = UUID.randomUUID().toString(true);
                Map<String, Object> userMap = BeanUtil.beanToMap(user,new HashMap<>(),
                        CopyOptions.create().setIgnoreNullValue(true).
                                setFieldValueEditor((fieldName,fieldValue)->fieldValue.toString()));
                //t3--???????????????redis???
                stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY+token,userMap);
                log.warn("--------------redis??????token????????????-------------------");
                log.warn(RedisConstants.LOGIN_USER_KEY+token);
                //??????token????????????
                stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token, RedisConstants.LOGIN_USER_TTL,TimeUnit.SECONDS);
                //??????token
                try {
                    clientController.connectToClient(user.getId()+"");
                }
                finally {
                    return Result.ok(token);
                }
            }else {
                return Result.fail("?????????????????????????????????");
            }
        }
        return Result.fail("??????????????????????????????");
    }

    @Override
    public Result insertUserForRegister(RegisterInfoDTO registerInfoDTO) {
        log.warn(registerInfoDTO.getEmail());
        log.warn(registerInfoDTO.getPhone());
        log.warn(registerInfoDTO.getEmailCode());
        //?????????????????????????????????????????????
        if (RegexUtils.isEmailInvalid(registerInfoDTO.getEmail())||RegexUtils.isPhoneInvalid(registerInfoDTO.getPhone())||RegexUtils.isPasswordInvalid(registerInfoDTO.getPassword())) {
            return Result.fail("???????????????????????????????????????????????????");
        }
        //?????????????????????????????????????????????
        String checkPhoneCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + registerInfoDTO.getPhone());
        String checkEmailCode = stringRedisTemplate.opsForValue().get(RedisConstants.EMAIL_CODE_KEY + registerInfoDTO.getEmail());
        if ((registerInfoDTO.getEmailCode().equals(checkEmailCode))&&(registerInfoDTO.getPhoneCode().equals(checkPhoneCode))){
            registerInfoDTO.setName(UUID.randomUUID()+"");
            int insert = userMapper.insert(registerInfoDTO);
            log.warn(insert+"");
            if (insert>0) {
                LoginFormDTO loginFormDTO=new LoginFormDTO();
                loginFormDTO.setCodePhoneOrEmail(registerInfoDTO.getEmail());
                loginFormDTO.setPassword(registerInfoDTO.getPassword());
                Result result = selectUserByCodeAndPassword(loginFormDTO, null);
                try {
                    User user = userMapper.selectOne(new QueryWrapper<User>().eq("phone", loginFormDTO.getCodePhoneOrEmail()).or().eq("email", loginFormDTO.getCodePhoneOrEmail()));
                    clientController.connectToClient(user.getId()+"");
                }finally {
                    return result;
                }
            }
        }
        return Result.fail("???????????????");
    }

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //???????????????
        if (RegexUtils.isPhoneInvalid(phone)){
            log.warn(phone);
            return Result.fail("?????????????????????");
        }
        //??????????????????????????????
        String checkCode = RandomUtil.randomNumbers(6);
        //?????????redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY+phone,checkCode,RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        log.warn("????????????????????????,?????????{}",checkCode);
        return Result.ok(checkCode);
    }


    @Override
    public Result sendMailCode(String mail) {
        //???????????????
        if (RegexUtils.isEmailInvalid(mail)){
            log.info(mail);
            return Result.fail("?????????????????????");
        }
        //???????????????????????????????????????
        String from= EmailConstants.EMAIL_SEND_FROM;
        String to=mail;
        String subject=EmailConstants.EMAIL_SUBJECT;
        MimeMessage message=javaMailSender.createMimeMessage();
        MimeMessageHelper helper= null;
        try {
            helper = new MimeMessageHelper(message,true);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Result.fail("????????????1");
        }
        try {
            helper.setFrom(from+EmailConstants.EMAIL_SEND_USER);
            helper.setTo(to);
            helper.setSubject(subject);
            String checkCode = RandomUtil.randomNumbers(6);
            log.debug(checkCode);
            //?????????redis
            stringRedisTemplate.opsForValue().set(RedisConstants.EMAIL_CODE_KEY+mail,checkCode,RedisConstants.EMAIL_CODE_TTL, TimeUnit.MINUTES);
            helper.setText(checkCode,true);
            //????????????
            //File file=new File("C:\\Users\\11601\\OneDrive\\??????\\l5.jpg");
            javaMailSender.send(message);
            //????????????
            return Result.ok(checkCode);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Result.fail("????????????2");
        }
    }

    @Override
    @Transactional
    public Result completePersonal(UserComplete userComplete) {
        log.warn(userComplete.getCompany().toString());
        log.warn(userComplete.getUserDTO().toString());
        int udp = userMapper.updateById(userComplete.getUserDTO());
        int insert=userInfoPoMapper.insert(userComplete.getCompany());
        if (RegisterException.registerResult(insert)&&RegisterException.registerResult(udp)) {
            return Result.ok(userComplete);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        log.warn("????????????");
        return Result.fail("????????????",404);
    }

    @Override
    public Result meDetail(int id) {
        QueryWrapper<UserInfoPo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",id);
        User user = userMapper.selectById(id);
        UserInfoPo userInfoPo = userInfoPoMapper.selectOne(queryWrapper);
        List<Object> retList=new ArrayList<>();
        retList.add(user);
        retList.add(userInfoPo);
        return Result.ok(retList, (long) retList.size());
    }


}

