//package com.springcloud.learn.config;
//
//import com.springcloud.learn.filter.LogoutFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @Description:  登出过滤器配置
// * @author kingson
// * @date
// **/
//@Configuration
//public class ClientFilterConfig {
//
//    @Bean
//    public FilterRegistrationBean registrationLogoutBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        // 指定过滤器
//        registrationBean.setFilter(new LogoutFilter());
//        // 指定访问url
//        registrationBean.addUrlPatterns("/logout");
//        // 设置名称
//        registrationBean.setName("LogoutFilter");
//        // 设置顺序
//        registrationBean.setOrder(1);
//
//        return registrationBean;
//    }
//}