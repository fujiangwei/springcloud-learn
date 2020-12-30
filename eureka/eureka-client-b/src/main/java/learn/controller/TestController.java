package learn.controller;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author
 * @Description:
 * @date
 **/
@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient client;

    @GetMapping("hello")
    public String hello() {
        // 通过服务名获取提供者实例信息
        List<ServiceInstance> instances = client.getInstances("kingson-eureka-client-provider");
        if (CollectionUtils.isNotEmpty(instances)) {
            ServiceInstance serviceInstance = instances.get(0);
            return restTemplate.getForObject("http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/test", String.class);
        }

        List<String> services = client.getServices();
        for (String service : services) {
            client.getInstances(service);
        }

        return "hello, hello!";
    }
}