package com.springcloud.learn.listener;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @Description: 文件描述
 * @author kingson
 * @date
 **/
@WebListener
@Slf4j(topic = "LogoutListener")
public class LogoutListener implements HttpSessionListener {

    @Override
    public synchronized void sessionCreated(HttpSessionEvent sessionEvent) {
        // log.info("LogoutListener sessionCreated sessionIs: {}", sessionEvent.getSession().getId());
    }

    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent sessionEvent) {
        // log.info("LogoutListener sessionDestroyed sessionIs: {}", sessionEvent.getSession().getId());
    }
}