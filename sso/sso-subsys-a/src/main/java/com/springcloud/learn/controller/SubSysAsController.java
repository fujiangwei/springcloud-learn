package com.springcloud.learn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 子系统A控制器
 *
 * @author kingson
 * @date
 **/
@Controller
public class SubSysAsController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

//    @GetMapping("/logout")
//    @ResponseBody
//    public String logout(String token, HttpServletRequest request, HttpServletResponse response) {
//        System.out.println("111");
//
//        return "logout";
//    }
}