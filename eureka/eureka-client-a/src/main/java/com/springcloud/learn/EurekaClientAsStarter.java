package com.springcloud.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description:
 * @author
 * @date
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class EurekaClientAsStarter {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientAsStarter.class, args);
    }
}