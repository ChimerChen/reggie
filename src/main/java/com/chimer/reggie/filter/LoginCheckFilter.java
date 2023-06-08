package com.chimer.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.chimer.reggie.common.BaseContext;
import com.chimer.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        log.info("拦截到请求：{}",request.getRequestURI());
        String url = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/user"
        };

        boolean check = check(urls, url);

        if (check) {
            log.info("本次请求{}不需要处理",url);
            filterChain.doFilter(request,response);
            return;
        }

        //判断后台用户是否登录
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            log.info("用户已登录，id为{}",empId);
            filterChain.doFilter(request,response);
            return;
        }

        //判断前台用户是否登录
        if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            log.info("用户已登录，id为{}",userId);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }

    public boolean check(String[] urls,String requestURl){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURl);
            if(match){
                return true;
            }
        }
        return false;
    }
}
