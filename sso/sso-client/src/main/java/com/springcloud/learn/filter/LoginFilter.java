package com.springcloud.learn.filter;

import com.springcloud.learn.constants.StrConsts;
import com.springcloud.learn.storage.StorageSingleton;
import com.springcloud.learn.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kingson
 * @Description: 登录过滤器，使用spring注解@Component使其生效
 * 一个 Filter 程序就是一个 Java 类，这个类必须实现 Filter 接口。
 * javax.servlet.Filter 接口中定义了三个方法：init、doFilter、destory。
 * @date
 **/
@Component
@Slf4j(topic = "LoginFilter")
public class LoginFilter implements Filter {

    /**
     * 运行环境信息对象
     */
    private FilterConfig filterConfig;

    /**
     * 单点登录后台服务地址
     */
    @Value("${sso.server}")
    private String ssoServer;

    /**
     * Web 容器创建 Filter 的实例对象后，将立即调用该 Filter 对象的 init 方法。
     * init 方法在 Filter 生命周期中仅被执行一次，Web 容器在调用 init 方法时，会传递一个包含 Filter 的配置和运行环境信息的 FilterConfig 对象。
     * <p>
     * 可以在 init 方法中完成与构造方法类似的初始化功能.
     * 要注意的是：如果初始化代码要使用到 FilterConfig 对象，这些代码只能在 init 方法中编写，而不能在构造方法中编写（尚未调用 init 方法，即并没有创建 FilterConfig 对象，要使用它则必然出错）
     *
     * @param filterConfig 运行环境信息对象
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        // 转换为http请求
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String localAddr = request.getLocalAddr();
        int localPort = request.getLocalPort();

        // 退出请求
        if (StringUtils.equals(StrConsts.LOGIN_OUT_URI, request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // session信息
        HttpSession session = request.getSession();
        // 已登录则进行后续处理,浏览器与子系统间的局部会话信息
        Object isLoginFlag = session.getAttribute(StrConsts.IS_LOGIN_FLAG);
        // log.info("客户端URI：{}与浏览器的sessionId：{}, isLogin: {}", ((HttpServletRequest) req).getRequestURI(), session.getId(), isLoginFlag);
        if (null != isLoginFlag && Boolean.parseBoolean(String.valueOf(isLoginFlag))) {
            // 说明浏览器已经登录过，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 请求是否有带token参数，如果有token的话，说明可能是sso-server发出的回调请求，或者伪造的请求，需要去sso认证中心进行验证
        String token = request.getParameter(StrConsts.TOKEN_KEY);
        if (StringUtils.isNotEmpty(token)) {
            // 用token去sso认证中心进行校验
            Map<String, String> paramMap = new HashMap<String, String>(16);
            paramMap.put(StrConsts.TOKEN_KEY, token);
            paramMap.put(StrConsts.CLIENT_URL_KEY, HttpUtils.wrapReqUrl(StrConsts.HTTP_PROTOCOL, localAddr + ":" + localPort, StrConsts.LOGIN_OUT_URI));
            // log.info("tokenVerify paramMap  = " + paramMap);
            String tokenVerifyR = HttpUtils.doGet(HttpUtils.wrapReqUrl(StrConsts.HTTP_PROTOCOL, ssoServer, StrConsts.VERIFY_TOKEN_URI), paramMap);
            log.info("tokenVerify result = " + tokenVerifyR);
            // 校验通过
            if (!StringUtils.equals("-1", tokenVerifyR)) {
                // 浏览器与子系统之间的会话信息设置
                // 设置为已登录
                session.setAttribute(StrConsts.IS_LOGIN_FLAG, Boolean.TRUE);
                // 设置token信息
                session.setAttribute(StrConsts.TOKEN_KEY, token);

                // 存储会话信息，用于注销
                StorageSingleton.getInstance().setSession(token, session);

                filterChain.doFilter(request, response);
                return;
            }
        }

        // 重定向至sso认证中心的登录页面，并附带当前请求地址
        String redirectUri = "http://" + ssoServer + "?" + StrConsts.CLIENT_URL_KEY + "=" + request.getRequestURL();
        response.sendRedirect(redirectUri);
    }

    @Override
    public void destroy() {
        System.out.println("this is LoginFilter destroy method");
        this.filterConfig = null;
    }
}