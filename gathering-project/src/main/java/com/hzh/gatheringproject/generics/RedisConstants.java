package com.hzh.gatheringproject.generics;

/**
 * @author DAHUANG
 * @date 2022/7/6
 */
public class RedisConstants {


    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final long LOGIN_CODE_TTL = 2L;

    public static final String EMAIL_CODE_KEY = "email:code:";
    public static final long EMAIL_CODE_TTL = 5L;

    public static final String REGISTER_CODE_KEY="register:phone:code:";
    public static final String USER="user:";

    public static final String LOGIN_USER_KEY="login:token:";
    public static final Long LOGIN_USER_TTL=1800L;
}
