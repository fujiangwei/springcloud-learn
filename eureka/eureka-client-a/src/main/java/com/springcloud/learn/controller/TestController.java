package com.springcloud.learn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @author
 * @date
 **/
@RestController
public class TestController {

    @Autowired
    private DiscoveryClient client;

    @GetMapping("test")
    public String test() {
        List<String> clientServices = client.getServices();
        System.out.println("clientServices = " + clientServices);
        return "hello, eureka!";
    }
}