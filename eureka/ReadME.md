#Eureka
一、简介

Eureka是Netflix开发的服务发现框架，本身是一个基于REST的服务，主要用于定位运行在AWS域中的中间层服务，以达到负载均衡和中间层服务故障转移的目的。SpringCloud将它集成在其子项目spring-cloud-netflix中，以实现SpringCloud的服务发现功能。

二、Eureka组件Eureka Server和Eureka Client
1. Eureka Server
* Eureka Server提供服务注册服务，各个节点启动后，会在Eureka Server中进行注册，这样Eureka Server中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中看到。
* Eureka Server本身也是一个服务，默认情况下会自动注册到Eureka注册中心。可以通过设置eureka.client.register-with-eureka=false不注册，如果搭建单机版的Eureka Server注册中心，则需要配置取消Eureka Server的自动注册逻辑。当前服务注册到当前服务代表的注册中心中是一个说不通的逻辑。
* Eureka Server通过Register、Get、Renew等接口提供服务的注册、发现和心跳检测等服务。

2. Eureka Client
* Eureka Client是一个java客户端，用于简化与Eureka Server的交互，客户端同时也具备一个内置的、使用轮询(round-robin)负载算法的负载均衡器。在应用启动后，将会向Eureka Server发送心跳,默认周期为30秒，如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，Eureka Server将会从服务注册表中把这个服务节点移除(默认90秒)。
* Eureka Client分为两个角色，分别是Service Provider和Service Consumer：
    * Service Provider
　　服务提供方，是注册到Eureka Server中的服务。
    * Service Consumer
　　服务消费方，通过Eureka Server发现服务，并消费。

    Provider在提供服务的同时，也可以消费其他Provider提供的服务；Consumer在消费服务的同时，也可以提供对外服务。

3. 框架
* Register(服务注册)：Eureka Client(Service Provider)把自己的IP和端口注册给Eureka-server。
* Renew(服务续约)：Eureka Client(Service Provider)向Eureka Server发送心跳包，每30秒发送一次,告诉Eureka-server自己还活着。
* Cancel(服务下线)：当Service Provider关闭时会向Eureka Server发送消息，让Eureka Server把自己从服务列表中删除，防止Service Consumer调用到不存在的服务。
* Get Registry(获取服务注册列表)：Eureka Client(Service Consumer)获取其他服务列表。
* Replicate(集群中数据同步)：Eureka-server集群中的数据复制与同步。
* Make Remote Call(远程调用)：完成服务的远程调用Eureka Client(Service Consumer) -> Eureka Client(Service Provider)。

三、Springboot 2.X搭建Eureka(单机模式)
* springboot2.X

    **Eureka Server**
    
    pom依赖：

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
    
    application.properties：    
    
        # 服务注册中心端口号
        server.port=8888
        # 服务注册中心实例的主机名
        eureka.instance.hostname=localhost
        # 是否向服务注册中心注册自己
        eureka.client.register-with-eureka=false
        # 是否检索服务
        eureka.client.fetch-registry=false
        # 服务注册中心的配置内容，指定服务注册中心的位置
        eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/
        
    应用启动类加注解@EnableEurekaServer
    
        @SpringBootApplication
        // 开启
        @EnableEurekaServer
        public class EurekaServerStarter {...}
        
    如上启动应用，访问http://localhost:8888就能查看Eureka的管理界面了，直接访问http://localhost:8888/eureka是不行的    
    
    **Eureka Client(Service Provider)**
    
    pom依赖：
    
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    
    application.properties：
    
        # 应用名称
        spring.application.name=kingson-eureka-client-provider
        # 服务注册中心端口号
        server.port=8081
        # 服务注册中心实例的主机名
        eureka.instance.hostname=localhost
        # 服务注册中心的配置内容，指定服务注册中心的位置
        eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:8888/eureka/
        
    应用启动类加注解@EnableDiscoveryClient
    
        @SpringBootApplication
        @EnableDiscoveryClient
        public class EurekaClientAsStarter {...}
        
    测试服务接口：
        
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
        
    如上配置好后启动应用，查看服务是否有注册到Eureka Server,在Eureka Server界面上查看实例
    
    **Eureka Client(Service Consumer)**
        
        pom依赖：
        
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            </dependency>
        
        application.properties：
        
            # 应用名称
            spring.application.name=kingson-eureka-client-consumer
            # 服务注册中心端口号
            server.port=8082
            # 服务注册中心实例的主机名
            eureka.instance.hostname=localhost
            # 服务注册中心的配置内容，指定服务注册中心的位置
            eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:8888/eureka/
            
        应用启动类加注解@EnableDiscoveryClient
        
            @SpringBootApplication
            @EnableDiscoveryClient
            public class EurekaClientBsStarter {
            
                /**RestTemplate配置*/
                @Bean
                public RestTemplate restTemplate(ClientHttpRequestFactory factory){
                    return new RestTemplate(factory);
                }
            
                /**RestTemplate配置*/
                @Bean
                public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(15000);
                    factory.setReadTimeout(5000);
                    return factory;
                }
                    
                ...
            }
            
        测试服务接口：
            
            @RestController
            public class TestController {
                /**RestTemplate*/
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
                        // RestTemplate发起http rest请求
                        return restTemplate.getForObject("http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/test", String.class);
                    }
                    
                    List<String> services = client.getServices();
                    for (String service : services) {
                        client.getInstances(service);
                    }
            
                    return "hello, hello!";
                }
            }
    如上配置好后启动应用，查看服务是否有注册到Eureka Server,在Eureka Server界面上查看实例，并通过访问http://127.0.0.1:8082/hello查看是否有调用到服务提供者服务
    
    
* Spring Cloud Commons 提供的抽象
    
    最早的时候服务发现注册都是通过DiscoveryClient来实现的。
    随着版本变迁把DiscoveryClient服务注册抽离出来变成了ServiceRegistry抽象，专门负责服务注册，
    DiscoveryClient专门负责服务发现，还提供了负载均衡的发现LoadBalancerClient抽象，DiscoveryClient通过@EnableDiscoveryClient的方式进行启用。
    

        
    
