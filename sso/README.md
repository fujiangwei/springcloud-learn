#单点登录流程
* 登录
1. 用户访问系统1的受保护资源，系统1发现用户未登录，跳转至sso认证中心，并将自己的地址作为参数
2. sso认证中心发现用户未登录，将用户引导至登录页面,用户输入用户名密码提交登录申请
3. sso认证中心校验用户信息，创建用户与sso认证中心之间的会话，称为全局会话，同时创建授权令牌
4. sso认证中心带着令牌跳转会前面系统的请求地址
5. 系统拿到令牌，去sso认证中心校验令牌是否有效
6. sso认证中心校验令牌，返回有效，注册系统
7. 系统使用该令牌创建与用户的会话，称为局部会话，返回受保护资源
用户访问系统2的受保护资源
8. 系统2发现用户未登录，跳转至sso认证中心，并将自己的地址作为参数
9. sso认证中心发现用户已登录，跳转回系统2的地址，并附上令牌
10. 系统2拿到令牌，去sso认证中心校验令牌是否有效
11. sso认证中心校验令牌，返回有效，注册系统2
12. 系统2使用该令牌创建与用户的局部会话，返回受保护资源

    用户登录成功之后，会与sso认证中心及各个子系统建立会话，用户与sso认证中心建立的会话称为全局会话，用户与各个子系统建立的会话称为局部会话，局部会话建立之后，用户访问子系统受保护资源将不再通过sso认证中心，全局会话与局部会话有如下约束关系:
    * 局部会话存在，全局会话一定存在
    * 全局会话存在，局部会话不一定存在
    * 全局会话销毁，局部会话必须销毁

* 登出
1. 用户向系统1发起注销请求
2. 系统1根据用户与系统1建立的会话id拿到令牌，向sso认证中心发起注销请求
3. sso认证中心校验令牌有效，销毁全局会话，同时取出所有用此令牌注册的系统地址
4. sso认证中心向所有注册系统发起注销请求
5. 各注册系统接收sso认证中心的注销请求，销毁局部会话
6. sso认证中心引导用户至登录页面

#Springboot 使用Filter
* 直接实现Filter接口，并使用@Component注解标注为组件自动注入bean

        @Component
        public class LoginFilter implements Filter 
    
* 实现Filter接口，用@WebFilter注解，指定拦截路径以及一些参数，同时需要在启动类使用@ServletComponentScan扫描带@WebFilter、@WebServlet、@WebListener并将帮我们注入bean.

    过滤器类设置@WebFilter：
    
        /**
        * filterName为过滤器名称，urlPatterns为访问url正则表达式
        */
        @WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
        public class LoginFilter implements Filter
        
    启动类加上@ServletComponentScan注解
    
        /**
        * @SpringBootApplication:@SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan
        * @ServletComponentScan 指定servlet扫描的包路径，如果不指定具体路径则自动扫描与当前类的同包以及子包
        */
        @SpringBootApplication
        @ServletComponentScan(basePackages = {"com.springcloud.learn.filter"})
        public class SubSysAStater


* 写一个配置类，使用@Configuration注解，并在类中使用@Bean注解注入FilterRegistrationBean对象，设置过滤器为指定的过滤器。

        @Configuration
        public class LoginFilterConfig {
        
            @Bean
            public FilterRegistrationBean registrationBean() {
                FilterRegistrationBean registrationBean = new FilterRegistrationBean();
                // 指定过滤器
                registrationBean.setFilter(new LoginFilter());
                // 指定访问url
                registrationBean.addUrlPatterns("/*");
                // 设置名称
                registrationBean.setName("LoginFilter");
                // 设置顺序
                registrationBean.setOrder(1);
                
                return registrationBean;
            }
        }
      
#deploy
1、application.yml

* deploy目录下的application.yml配置文件为服务启动时加载的配置文件，通过jfiles参数进行指定，这样项目启动时将加载指定的配置文件进行启动，优先级高于jar包中的resources下的配置文件
* 可以在指定的外部application.yml配置文件中指定具体加载哪个配置文件（dev/prod/test）,如下：
    
        spring:
          profiles:
            active: prod
            
也可以在命令启动行通过--spring.profiles.active=xxx进行指定。

        --spring.profiles.active=prod
2、start.sh 在linux服务器上的启动命令

3、stop.sh 在linux服务器上的停止命令
  
#测试注意点
* 测试过程中发现认证中心不能和子系统在同一台服务上，否则认证中心的会话会发生变化，无法实现单点登录效果
* 子系统可以部署在同一台服务器上