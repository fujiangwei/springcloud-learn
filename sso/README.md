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