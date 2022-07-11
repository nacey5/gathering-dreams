package com.hzh.gatheringproject.interceptor;

import com.hzh.gatheringproject.util.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author DAHUANG
 * @date 2022/7/11
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否需要拦截（threadLocal中是否有用户）
        if (UserHolder.getUser() == null) {
            //没有，进行拦截
            response.setStatus(401);
            return false;
        }
        //有用户，放行
        return true;
    }
}
