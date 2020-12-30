package com.springcloud.learn.filter;

import com.google.common.collect.Maps;
import com.springcloud.learn.constants.StrConsts;
import com.springcloud.learn.storage.StorageSingleton;
import com.springcloud.learn.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:  登出过滤器，使用spring注解@Component使其生效
 * @author kingson
 * @date
 **/
@Component
@Slf4j(topic = "LogoutFilter")
public class LogoutFilter implements Filter {

    private FilterConfig filterConfig;

    /**
     * 单点登录后台服务地址
     */
    @Value("${sso.server}")
    private String ssoServer;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (!StringUtils.equals(StrConsts.LOGIN_OUT_URI, request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 会话信息
        HttpSession session = request.getSession();
        // token信息
        String token = request.getParameter(StrConsts.LOGIN_OUT_KEY);
        // 子系统主动退出请求
        if (StringUtils.isEmpty(token)) {
            // 子系统与浏览器之间的会话信息获取token
            token = (String)session.getAttribute(StrConsts.TOKEN_KEY);
            log.info("子系统主动注销请求，token: {}", token);
            // 向认证中心发送注销请求
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put(StrConsts.LOGIN_OUT_KEY, token);
            HttpUtils.doGet(HttpUtils.wrapReqUrl(StrConsts.HTTP_PROTOCOL, ssoServer, StrConsts.LOGIN_OUT_URI), paramMap);
            // 获取并删除会话信息
            session = StorageSingleton.getInstance().getAndRemoveSession(token);
            // 注销本地会话
            if (null != session) {
                log.info("LogoutFilter sessionId：{}将被注销", session.getId());
                // 会话失效
                session.invalidate();
                log.info("session：{}已被注销, sessionMap: {}", session, StorageSingleton.getInstance().getSession(token));
            }

            // 注销后重定向 跳转至登录页
            String localAddr = request.getLocalAddr();
            int localPort = request.getLocalPort();
            // 首页返回地址
            String serviceIndex = StrConsts.CLIENT_URL_KEY + "=" + HttpUtils.wrapReqUrl(StrConsts.HTTP_PROTOCOL,
                    localAddr + ":" + localPort, StrConsts.SERVICE_INDEX);
            response.sendRedirect(HttpUtils.wrapReqUrl(StrConsts.HTTP_PROTOCOL, ssoServer, StrConsts.LOGIN_INDEX_URI) + "?" + serviceIndex);
            return;
        }

        // 校验token是否有效
       /* boolean tokenVerify = this.tokenVerify(token);
        if (!tokenVerify) {
            log.info("token无效");
            return;
        }*/

        // 存储会话信息
        HttpSession sessionExisted = StorageSingleton.getInstance().getAndRemoveSession(token);
        log.info("子系统被动注销请求token: {}，sessionExisted：{}", token, sessionExisted);
        if (null != sessionExisted) {
            sessionExisted.invalidate();
        }
    }

    @Override
    public void destroy() {
        // System.out.println("this is LogoutFilter doFilter method");
        this.filterConfig = null;
    }

    private boolean tokenVerify(String token) {
        // 用token去sso认证中心进行校验
        Map<String, String> paramVerifyMap = new HashMap<String, String>(16);
        Assert.isTrue(StringUtils.isNotEmpty(token), "token不能为空");
        boolean tokenVerify = Boolean.TRUE;
        try {
            paramVerifyMap.put(StrConsts.TOKEN_KEY, token);
            String tokenVerifyR = HttpUtils.doGet(HttpUtils.wrapReqUrl(StrConsts.HTTP_PROTOCOL, ssoServer, StrConsts.VERIFY_TOKEN_URI), paramVerifyMap);
            log.info("tokenVerify result = " + tokenVerifyR);
            if (StringUtils.equals("-1", tokenVerifyR)) {
                log.info("注销本地会话token无效");
                tokenVerify = Boolean.FALSE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tokenVerify;
    }
 }