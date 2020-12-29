package com.springcloud.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: 子系统A服务运行入口
 * @author kingson
 **/
@SpringBootApplication
public class SubSysAsStater {

    public static void main(String[] args) {
        SpringApplication.run(SubSysAsStater.class, args);
    }
}