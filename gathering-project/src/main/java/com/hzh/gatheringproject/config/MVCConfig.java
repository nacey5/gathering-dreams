package com.hzh.gatheringproject.config;

import com.hzh.gatheringproject.interceptor.LoginInterceptor;
import com.hzh.gatheringproject.interceptor.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author DAHUANG
 * @date 2022/7/11
 */
@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登陆拦截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/",
                        "/user/code",
                        "/user/login",
                        "/user/register",
                        "/user/emailCode",
                        "/upload/icon",
                        "/user/qita2/**",
                        "/user/qita3/**"
                ).order(1);

        //token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
    }
}
