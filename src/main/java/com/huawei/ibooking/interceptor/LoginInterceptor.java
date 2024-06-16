package com.huawei.ibooking.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.huawei.ibooking.model.MyResponseBody;
import com.huawei.ibooking.model.StudentDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        StudentDO user = (StudentDO) session.getAttribute("user");
        if (user != null)
            return true;

        sendJson(response, JSONObject.toJSONString(new MyResponseBody("408", "need login", null)));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    public void sendJson(HttpServletResponse response, String json) {
        response.setContentType("application/json;charset=utf-8\"");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
            response.flushBuffer();
        } catch (IOException e) {
            log.info("error to send json: " + json);
        }
    }
}
