package com.springcloud.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author
 * @Description:
 * @date
 **/
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerStarter {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerStarter.class, args);
    }
}