package com.springcloud.learn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: 子系统B控制器
 *
 * @author kingson
 * @date
 **/
@Controller
public class SubSysBsController {

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
//    public String logout() {
//        return "logout";
//    }

}