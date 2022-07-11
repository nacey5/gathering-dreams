package com.hzh.gatheringproject.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hzh.gatheringproject.dto.UserDTO;
import com.hzh.gatheringproject.generics.FrontEndConstants;
import com.hzh.gatheringproject.generics.RedisConstants;
import com.hzh.gatheringproject.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author DAHUANG
 * @date 2022/7/11
 */
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate redisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.warn("------------RefreshTokenInterceptor------------");
        //获取请求头中的token
        String token = request.getHeader(FrontEndConstants.REQUEST_HEADER);
        //如果token为空
        if (StrUtil.isBlank(token)) {
            return true;
        }
        //获取token中的用户
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token);
        //判断用户是否存在
        if (userMap.isEmpty()) {
            return true;
        }
        //将查询的hash数据转为UserDTO的对象
        UserDTO userDTO= BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        //存在，保存用户信息到threadLocal
        UserHolder.saveUser(userDTO);
        //刷新token的有效期
        redisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token,RedisConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);
        //放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除用户
        UserHolder.removeUser();
    }
}
