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

#CSRF 防御常规思路
* 验证 HTTP Referer 字段

    根据 HTTP 协议，在 HTTP 头中有一个字段叫 Referer，它记录了该 HTTP 请求的来源地址。在通常情况下，访问一个安全受限页面的请求来自于同一个网站，
比如需要访问 http://test.example，用户必须先登陆 test.example，然后通过点击页面上的按钮来触发转账事件。
这时，该转帐请求的 Referer 值就会是转账按钮所在的页面的 URL，通常是以 test.example 域名开头的地址。
而如果黑客要对银行网站实施 CSRF 攻击，他只能在他自己的网站构造请求，当用户通过黑客的网站发送请求到银行时，该请求的 Referer 是指向黑客自己的网站。
因此，要防御 CSRF 攻击，银行网站只需要对于每一个转账请求验证其 Referer 值，如果是以 bank.example 开头的域名，则说明该请求是来自银行网站自己的请求，是合法的。
如果 Referer 是其他网站的话，则有可能是黑客的 CSRF 攻击，拒绝该请求。
* 在请求地址中添加 token 并验证

    CSRF 攻击之所以能够成功，是因为黑客可以完全伪造用户的请求，该请求中所有的用户验证信息都是存在于 cookie 中，因此黑客可以在不知道这些验证信息的情况下直接利用用户自己的 cookie 来通过安全验证。要抵御 CSRF，关键在于在请求中放入黑客所不能伪造的信息，并且该信息不存在于 cookie 之中。可以在 HTTP 请求中以参数的形式加入一个随机产生的 token，并在服务器端建立一个拦截器来验证这个 token，如果请求中没有 token 或者 token 内容不正确，则认为可能是 CSRF 攻击而拒绝该请求。
* 在 HTTP 头中自定义属性并验证

    这种方法也是使用 token 并进行验证，和上一种方法不同的是，这里并不是把 token 以参数的形式置于 HTTP 请求之中，而是把它放到 HTTP 头中自定义的属性里。通过 XMLHttpRequest 这个类，可以一次性给所有该类请求加上 csrftoken 这个 HTTP 头属性，并把 token 值放入其中。这样解决了上种方法在请求中加入 token 的不便，同时，通过 XMLHttpRequest 请求的地址不会被记录到浏览器的地址栏，也不用担心 token 会透过 Referer 泄露到其他网站中去。
然而这种方法的局限性非常大。XMLHttpRequest 请求通常用于 Ajax 方法中对于页面局部的异步刷新，并非所有的请求都适合用这个类来发起，而且通过该类请求得到的页面不能被浏览器所记录下，从而进行前进，后退，刷新，收藏等操作，给用户带来不便。另外，对于没有进行 CSRF 防护的遗留系统来说，要采用这种方法来进行防护，要把所有请求都改为 XMLHttpRequest 请求，这样几乎是要重写整个网站，代价很大。


参考：
* https://www.cnblogs.com/pengdai/p/12164754.html