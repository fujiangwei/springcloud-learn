#springboot2.0集成RestTemplate

1.RestTemplate的优缺点
* 优点：
连接池、超时时间设置、支持异步、请求和响应的编解码
* 缺点：
依赖别的spring版块、参数传递不灵活

2.引入
    
    pom依赖：
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
    RestTemplateConfig类：
        
        @Configuration
        public class RestTemplateConfig {
         
            @Bean
            public RestTemplate restTemplate(ClientHttpRequestFactory factory){
                return new RestTemplate(factory);
            }
         
            @Bean
            public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(15000);
                factory.setReadTimeout(5000);
                return factory;
            }
         
        }
        
    使用：
    
        @Service
        public class demoService {
         
            @Autowired
            private RestTemplate restTemplate;
         
            public String get(Integer id){
                return restTemplate.getForObject("http://localhost:8080/user?userId=id",String.class);
            }
        }
        
3.常用方法：
* delete() 在特定的URL上对资源执行HTTP DELETE操作
* exchange() 在URL上执行特定的HTTP方法，返回包含对象的ResponseEntity，这个对象是从响应体中映射得到的
* execute() 在URL上执行特定的HTTP方法，返回一个从响应体映射得到的对象
* getForEntity() 发送一个HTTP GET请求，返回的ResponseEntity包含了响应体所映射成的对象
* getForObject() 发送一个HTTP GET请求，返回的请求体将映射为一个对象
* postForEntity() POST 数据到一个URL，返回包含一个对象的ResponseEntity，这个对象是从响应体中映射得到的
* postForObject() POST 数据到一个URL，返回根据响应体匹配形成的对象
* headForHeaders() 发送HTTP HEAD请求，返回包含特定资源URL的HTTP头
* optionsForAllow() 发送HTTP OPTIONS请求，返回对特定URL的Allow头信息
* postForLocation() POST 数据到一个URL，返回新创建资源的URL
* put() PUT 资源到特定的URL

4.参考
* RestTemplate官方网站：https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html
* https://blog.csdn.net/weixin_40461281/article/details/83540604
* https://blog.csdn.net/QiaoRui_/article/details/80453799     
* https://www.xncoding.com/2017/07/06/spring/sb-restclient.html
* http://ju.outofmemory.cn/entry/341530
* https://my.oschina.net/lifany/blog/688889

关于配置http连接池原理的文章：
* https://blog.csdn.net/umke888/article/details/54881946
* https://www.cnblogs.com/likaitai/p/5431246.html