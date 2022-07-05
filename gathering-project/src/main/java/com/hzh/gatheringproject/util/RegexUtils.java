package com.hzh.gatheringproject.util;

import com.alibaba.druid.util.StringUtils;
import com.hzh.gatheringproject.generics.RegexPatterns;

/**
 * @author DAHUANG
 * @date 2022/7/6
 */
public class RegexUtils {

    /**
     * 验证是否是有效的手机号
     * @param phone
     * @return
     */
    public static boolean isPhoneInvalid(String phone){
        return mismatch(phone, RegexPatterns.PHONE_REGEX);
    }

    /**
     * 验证是否是有效的邮箱
     * @param email
     * @return
     */
    public static boolean isEmailInvalid(String email){
        return mismatch(email,RegexPatterns.EMAIL_REGEX);
    }

    /**
     * 验证密码是否有效
     * @param password
     * @return
     */
    public static boolean isPasswordInvalid(String password){
        return mismatch(password,RegexPatterns.PASSWORD_REGEX);
    }


    /**
     * 校验是否不符合正则格式
     * @param str
     * @param regex
     * @return
     */
    public static boolean mismatch(String str,String regex){
        if (StringUtils.isEmpty(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}
