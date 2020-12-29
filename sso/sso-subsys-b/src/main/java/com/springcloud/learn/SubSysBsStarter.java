package com.springcloud.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: 子系统B服务运行入口
 * @author kingson
 * @date
 **/
@SpringBootApplication
public class SubSysBsStarter {

    public static void main(String[] args) {
        SpringApplication.run(SubSysBsStarter.class, args);
    }
}