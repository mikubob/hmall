package com.hmall.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    //controller 方法执行前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取登录用户的信息
        String userInfo = request.getHeader("user-info");
        //2.判断用户是否存在
        if (StrUtil.isNotBlank(userInfo)) {
            //存在，将用户信息保存到ThreadLocal中
            UserContext.setUser(Long.valueOf(userInfo));
        }
        //3.放行
        return true;
    }

    //controller 方法执行后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理用户
        UserContext.removeUser();
    }
}
