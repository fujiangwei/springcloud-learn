package com.springcloud.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author kingson
 * @Description: 文件描述
 **/
@SpringBootApplication
@ServletComponentScan
public class SSOServerStarter {

    public static void main(String[] args) {
        SpringApplication.run(SSOServerStarter.class, args);
    }
}