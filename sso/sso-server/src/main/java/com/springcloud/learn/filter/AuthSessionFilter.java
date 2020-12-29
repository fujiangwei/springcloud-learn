package com.springcloud.learn.filter;

import com.springcloud.learn.constants.StrConsts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author kingson
 * @Description: 文件描述
 * @date
 **/
@WebFilter(filterName = "AuthSessionFilter", urlPatterns = "/*")
@Slf4j(topic = "SSO-AUTH-FILTER")
public class AuthSessionFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession();
        String uri = request.getRequestURI();

        // 过滤静态资源
        String url = uri + "?" + request.getQueryString();
        boolean filterRes = filterResUrl(url);
        if (filterRes) {
            // log.warn("当前url：{}为文件静态资源访问", url);
            return;
        }

        // 退出请求
        if (StringUtils.equals(StrConsts.LOGIN_OUT_URI, uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 认证请求
        if (StringUtils.equals(StrConsts.VERIFY_TOKEN_URI, uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 是否登录状态值
        Object isLoginFlag = session.getAttribute(StrConsts.IS_LOGIN_FLAG);
        /*log.info("客户端url：{},认证中心的doFilter中sessionId：{}}",
                request.getParameter(StrConsts.CLIENT_URL_KEY), session.getId());*/
        if (null != isLoginFlag && Boolean.parseBoolean(String.valueOf(isLoginFlag))) {
            // 已经登录，将token返回
            String token = (String) session.getAttribute(StrConsts.TOKEN_KEY);
            // 客户端请求url
            String clientUrl = request.getParameter(StrConsts.CLIENT_URL_KEY);

            if (StringUtils.isNotEmpty(clientUrl)) {
                String redirectUrl;
                if (clientUrl.contains(StrConsts.MARK_KEY)) {
                    redirectUrl = clientUrl + "&" + StrConsts.TOKEN_KEY + "=" + token;
                } else {
                    redirectUrl = clientUrl + "?" + StrConsts.TOKEN_KEY + "=" + token;
                }

                response.sendRedirect(redirectUrl);
                return;
            }
            // 认证中心自己登录认证跳转页
            if (!StringUtils.equals(StrConsts.SERVICE_INDEX, uri)) {
                response.sendRedirect("/index");
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.equals(StrConsts.LOGIN_URI, uri) || StringUtils.equals(StrConsts.LOGIN_INDEX_URI, uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.sendRedirect("/");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        // System.out.println("this is AuthSessionFilter destroy method");
    }

    private boolean filterResUrl(String url) {
        String[] urls = {"/json", ".js", ".css", ".ico", ".jpg", ".png"};
        boolean result = Boolean.FALSE;

        if (StringUtils.isEmpty(url)) {
            return result;
        }

        for (int i = 0; i < urls.length; i++) {
            if (url.indexOf(urls[i]) != -1) {
                result = Boolean.TRUE;
                break;
            }
        }

        return result;
    }
}