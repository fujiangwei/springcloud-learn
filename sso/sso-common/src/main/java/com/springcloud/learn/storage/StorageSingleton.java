package com.springcloud.learn.storage;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author kingson
 * @Description: 文件描述
 * @date
 **/
public class StorageSingleton {

    private Map<String, HttpSession> sessionMap = Maps.newConcurrentMap();

    private static class StorageSingleHolder {
        private static StorageSingleton singleton = new StorageSingleton();
    }

    public static StorageSingleton getInstance() {
        return StorageSingleHolder.singleton;
    }

    public void setSession(String token, HttpSession session) {
        System.out.println("会话信息 before setSession sessionMap " + sessionMap);
        sessionMap.put(token, session);
        System.out.println("会话信息 after setSession sessionMap " + sessionMap);
    }

    public HttpSession getSession(String token) {
        System.out.println("会话信息 getSession sessionMap " + sessionMap);
        if (StringUtils.isNotEmpty(token) && sessionMap.containsKey(token)) {
            return sessionMap.get(token);
        }

        return null;
    }

    public HttpSession getAndRemoveSession(String token) {
        System.out.println("会话信息 getAndRemoveSession sessionMap " + sessionMap);
        if (StringUtils.isNotEmpty(token) && sessionMap.containsKey(token)) {
            HttpSession remove = sessionMap.remove(token);
            System.out.println("会话信息 getAndRemoveSession after remove sessionMap " + sessionMap);
            return remove;
        }

        return null;
    }
}