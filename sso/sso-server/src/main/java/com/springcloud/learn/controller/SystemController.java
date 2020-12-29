package com.springcloud.learn.controller;

import com.google.common.collect.Maps;
import com.springcloud.learn.constants.StrConsts;
import com.springcloud.learn.storage.ClientStorageSingleton;
import com.springcloud.learn.storage.StorageSingleton;
import com.springcloud.learn.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author kingson
 * @Description: 系统登录登出等业务流程控制器
 * @date
 **/
@Controller
@Slf4j(topic = "SystemController")
public class SystemController {

    @PostMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 可以登录
        if (!StringUtils.equals(StrConsts.LOGIN_USERNAME, username)
                || !StringUtils.equals(StrConsts.LOGIN_PASSWORD, password)) {
            model.addAttribute("error_info", "用户名或密码不对");
            return "redirect:/";
        }

        // 当前会话信息
        HttpSession session = request.getSession();
        log.info("login中的sessionId：{}", session.getId());

        // 模拟会话token
        String token = UUID.randomUUID().toString();
        // 全局会话信息
        session.setAttribute(StrConsts.IS_LOGIN_FLAG, Boolean.TRUE);
        session.setAttribute(StrConsts.TOKEN_KEY, token);
        // 存储会话信息
        StorageSingleton.getInstance().setSession(token, session);
        // 客户端请求url
        String clientUrl = request.getParameter(StrConsts.CLIENT_URL_KEY);
        // 回调客户端url请求
        if (StringUtils.isNotEmpty(clientUrl)) {
            if (clientUrl.contains(StrConsts.MARK_KEY)) {
                return "redirect:" + clientUrl + "&" + StrConsts.TOKEN_KEY + "=" + token;
            } else {
                return "redirect:" + clientUrl + "?" + StrConsts.TOKEN_KEY + "=" + token;
            }
        }

        return "redirect:/";
    }

    @GetMapping("/verifyToken")
    @ResponseBody
    public String verifyToken(String token, String clientUrl, HttpServletRequest request) {
        HttpSession session = request.getSession();
        // log.info("verifyToken sessionId is {}, token is {}", session.getId(), token);
        // 看sso-server中是否存在该token，存在说明登录了
        if (StringUtils.isNotEmpty(token)) {
            // 获取token值(在登录时会将会话信息写入会话信息)
            HttpSession sessionExist = StorageSingleton.getInstance().getSession(token);
            // log.info("sessionExist is {}", sessionExist);
            if (null != sessionExist && null != sessionExist.getAttribute(StrConsts.TOKEN_KEY)) {
                String tokenExisted = sessionExist.getAttribute(StrConsts.TOKEN_KEY).toString();
                // log.info("tokenExisted is {}", tokenExisted);
                // 注册系统
                synchronized (token.intern()) {
                    // 存储客户端退出url
                    ClientStorageSingleton.getInstance().setClientStorage(token, clientUrl);
                    // log.info("存储客户端退出url : {}", ClientStorageSingleton.getInstance().getClientStorage(token));
                }

                // log.info("StringUtils.equals(token, tokenExisted) = " + StringUtils.equals(token, tokenExisted));
                return tokenExisted;
            }
        }

        return "-1";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 请求url中是否有token参数（有表示为子系统发起的注销请求，没有表示是认证中心发起的注销请求）
        String token = request.getParameter(StrConsts.LOGIN_OUT_KEY);
        HttpSession session;
        if (StringUtils.isEmpty(token)) {
            log.info("认证中心主动注销请求");
            // 当前的请求会话信息
            session = request.getSession();
            // 认证中心主动注销请求，token从当前会话信息中获取
            token = (String) session.getAttribute(StrConsts.TOKEN_KEY);
        } else {
            // 子系统发起的注销请求，会话信息为存储会话信息
            log.info("子系统发起的注销请求");
            session = StorageSingleton.getInstance().getAndRemoveSession(token);
        }

        if (null != session) {
            // 会话失效
            session.invalidate();
        }

        // 注销子系统
        Set<String> clientUrls = ClientStorageSingleton.getInstance().getAndRemoveClientStorage(token);
        log.info("注销子系统会话clientUrls: {}", clientUrls);
        if (!clientUrls.isEmpty()) {
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put(StrConsts.LOGIN_OUT_KEY, token);
            for (String clientUrl : clientUrls) {
                try {
                    String doPost = HttpUtils.doPost(clientUrl, paramMap);
                    log.info("注销子系统会话结束, clientUrl: {}, doPost: {}", clientUrl, doPost);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            log.info("不存在子系统登录,清除sessionMap{}会话信息", StorageSingleton.getInstance().getSession(token));
            HttpSession sessionStorage = StorageSingleton.getInstance().getSession(token);
            if (null != sessionStorage && sessionStorage == session) {
                log.info("正在清除token为{}的会话信息", token);
                StorageSingleton.getInstance().getAndRemoveSession(token);
            }
            log.info("清除完后sessionMap: {}", StorageSingleton.getInstance().getSession(token));
        }

        // 登录页面
        return "redirect:/";
    }
}