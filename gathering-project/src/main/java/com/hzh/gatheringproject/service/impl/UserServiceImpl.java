package com.hzh.gatheringproject.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzh.gatheringproject.dto.*;
import com.hzh.gatheringproject.entity.User;
import com.hzh.gatheringproject.entity.UserInfoPo;
import com.hzh.gatheringproject.exception.RegisterException;
import com.hzh.gatheringproject.generics.EmailConstants;
import com.hzh.gatheringproject.generics.RedisConstants;
import com.hzh.gatheringproject.generics.SystemConstants;
import com.hzh.gatheringproject.service.UserService;
import com.hzh.gatheringproject.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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


    @Override
    public Result selectUserByCodeAndPassword(LoginFormDTO loginFormDTO, HttpSession session) {

        if (loginFormDTO.getCheckCode()!=null&&!"".equals(loginFormDTO.getCheckCode())){
            List<User> users = null;
            //进行手机验证码登陆
            String checkCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY+loginFormDTO.getCodePhoneOrEmail());
            log.warn(checkCode);
            //如果验证成功，在sql中查找该用户
            if (loginFormDTO.getCheckCode().equals(checkCode)) {
                Map<String,Object> map=new HashMap<>();
                map.put(SystemConstants.CHECK_PHONE,loginFormDTO.getCodePhoneOrEmail());
                users = userMapper.selectByMap(map);
            }
            //存在，保存在redis中-》token
            if (users!=null&&users.size()>0) {
                User user = users.get(0);
                String token = UUID.randomUUID().toString(true);
                //这个的作用是为了解决long无法转换成string的异常-java.base/java.lang.Long cannot be cast to java.base/java.lang.String
                Map<String, Object> userMap = BeanUtil.beanToMap(user,new HashMap<>(),
                        CopyOptions.create().setIgnoreNullValue(true).
                                setFieldValueEditor((fieldName,fieldValue)->fieldValue.toString()));
                //t3--存储数据到redis中
                stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY+token,userMap);
                //设置token的有效期
                stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token, RedisConstants.LOGIN_USER_TTL,TimeUnit.SECONDS);
                //返回token
                return Result.ok(token);

            }
            //不存在，返回失败信息让其注册
            return Result.fail("您还没有注册或者验证码失败");
        }
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        //对账号格式进行检验，是否为邮箱或者为手机号码
        if (!RegexUtils.isEmailInvalid(loginFormDTO.getCodePhoneOrEmail())||!RegexUtils.isPhoneInvalid(loginFormDTO.getCodePhoneOrEmail())) {
            queryWrapper.eq("phone",loginFormDTO.getCodePhoneOrEmail()).or().eq("email",loginFormDTO.getCodePhoneOrEmail()).eq("password",loginFormDTO.getPassword());
            User user = userMapper.selectOne(queryWrapper);
            if (user != null&&user.getId()>0) {
                //保存在redis中->token令牌
                String token = UUID.randomUUID().toString(true);
                Map<String, Object> userMap = BeanUtil.beanToMap(user,new HashMap<>(),
                        CopyOptions.create().setIgnoreNullValue(true).
                                setFieldValueEditor((fieldName,fieldValue)->fieldValue.toString()));
                //t3--存储数据到redis中
                stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY+token,userMap);
                log.warn("--------------redis添加token用户数据-------------------");
                log.warn(RedisConstants.LOGIN_USER_KEY+token);
                //设置token的有效期
                stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token, RedisConstants.LOGIN_USER_TTL,TimeUnit.SECONDS);
                //返回token
                return Result.ok(token);
            }else {
                return Result.fail("密码和手机或邮箱不匹配");
            }
        }
        return Result.fail("手机或者邮箱格式错误");
    }

    @Override
    public Result insertUserForRegister(RegisterInfoDTO registerInfoDTO) {
        log.warn(registerInfoDTO.getEmail());
        log.warn(registerInfoDTO.getPhone());
        log.warn(registerInfoDTO.getEmailCode());
        //对手机号码和邮箱进行进一步检验
        if (RegexUtils.isEmailInvalid(registerInfoDTO.getEmail())||RegexUtils.isPhoneInvalid(registerInfoDTO.getPhone())||RegexUtils.isPasswordInvalid(registerInfoDTO.getPassword())) {
            return Result.fail("您的密码或者手机或者邮箱的格式错误");
        }
        //进行手机验证码和邮箱验证码验证
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
                return result;
            }
        }
        return Result.fail("验证码错误");
    }

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //校验手机号
        if (RegexUtils.isPhoneInvalid(phone)){
            log.warn(phone);
            return Result.fail("手机格式不正确");
        }
        //如果符合，生成验证码
        String checkCode = RandomUtil.randomNumbers(6);
        //保存到redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY+phone,checkCode,RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        log.warn("发送短信验证成功,验证码{}",checkCode);
        return Result.ok(checkCode);
    }


    @Override
    public Result sendMailCode(String mail) {
        //校验邮箱号
        if (RegexUtils.isEmailInvalid(mail)){
            log.info(mail);
            return Result.fail("邮箱格式不正确");
        }
        //如果符合，往邮箱发送验证码
        String from= EmailConstants.EMAIL_SEND_FROM;
        String to=mail;
        String subject=EmailConstants.EMAIL_SUBJECT;
        MimeMessage message=javaMailSender.createMimeMessage();
        MimeMessageHelper helper= null;
        try {
            helper = new MimeMessageHelper(message,true);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Result.fail("未知异常1");
        }
        try {
            helper.setFrom(from+EmailConstants.EMAIL_SEND_USER);
            helper.setTo(to);
            helper.setSubject(subject);
            String checkCode = RandomUtil.randomNumbers(6);
            log.debug(checkCode);
            //保存到redis
            stringRedisTemplate.opsForValue().set(RedisConstants.EMAIL_CODE_KEY+mail,checkCode,RedisConstants.EMAIL_CODE_TTL, TimeUnit.MINUTES);
            helper.setText(checkCode,true);
            //添加附件
            //File file=new File("C:\\Users\\11601\\OneDrive\\图片\\l5.jpg");
            javaMailSender.send(message);
            //成功发送
            return Result.ok(checkCode);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Result.fail("未知异常2");
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
        log.warn("完善失败");
        return Result.fail("修改失败",404);
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

