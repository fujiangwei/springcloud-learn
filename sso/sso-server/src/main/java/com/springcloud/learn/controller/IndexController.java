package com.springcloud.learn.controller;

import com.springcloud.learn.constants.StrConsts;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 文件描述
 * @author kingson
 **/
@Controller
public class IndexController {

    @GetMapping(value = "/")
    public String index(HttpServletRequest request, Model model) {
        model.addAttribute(StrConsts.CLIENT_URL_KEY, request.getParameter(StrConsts.CLIENT_URL_KEY));
        return "login";
    }

    @GetMapping("/index")
    public String success() {
        return "index";
    }
}