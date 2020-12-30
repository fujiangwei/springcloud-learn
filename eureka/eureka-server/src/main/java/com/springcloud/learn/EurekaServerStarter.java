package com.springcloud.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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

    /**
     * 新版（Spring Cloud 2.0 以上）的security默认启用了csrf检验，要在Eureka Server端配置security的csrf检验为false,否则Eureka Client将无法注册
     * Spring Security启用csrf，然后会对URL请求进行token验证，如果请求中没有token,浏览器会任务是非法网页的请求，然后就会拒绝
     */
    @EnableWebSecurity
    public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            System.out.println("禁用CSRF保护");
            // http.csrf().disable();
            // 禁用/eureka/**端点的这个请求,以便客户端能注册
            http.csrf().ignoringAntMatchers("/eureka/**");
            super.configure(http);
        }
    }

    /**
     * 安全访问开启
     */
    // @EnableWebSecurity
    /*public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    *//* allow *//*
                    .antMatchers("/plugins/**", "/api-docs/**") .permitAll()
                    .antMatchers("/login", "/logout").permitAll()

                    *//* auth control *//*
                    .antMatchers("/xxx/user", "/xxx/user/**").access("hasAuthority('xxx:user')")
                    .antMatchers("/xxx/role", "/xxx/role/**").access("hasAuthority('xxx:role')")

                    *//* others *//*
                    .anyRequest().authenticated()

                    *//* other Filters *//*
                    .and()
                    //.addFilterBefore(xxxFilter(), UsernamePasswordAuthenticationFilter.class)

                    *//* iframe *//*
                    .headers()
                    .frameOptions()
                    .sameOrigin()

                    *//* form login & logout *//*
                    .and().formLogin()
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/admin/", true)
                    .and().rememberMe()
                    .rememberMeParameter("remember")
                    .rememberMeCookieName("remember")
                    .and().logout()
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    // .logoutSuccessHandler(new XXXLogoutSuccessHandler(localeResolver()))
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .permitAll()

                    *//* csrf *//*
                    .and().csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                    // .and().cors()

            super.configure(http);
        }
    }*/


    /**
     * Spring Security - new CookieCsrfTokenRepository()
     * 可以通过new CookieCsrfTokenRepository()自定义拦截的逻辑
     */
    /*@EnableWebSecurity
    public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().csrfTokenRepository(new CookieCsrfTokenRepository())
                    .requireCsrfProtectionMatcher(
                            *//**
     * 拦截“/login”开头的访问路径，不让访问
     * 拦截所有“POST”请求，不让访问
     *//*
                        httpServletRequest -> httpServletRequest.getRequestURI().startsWith("/login")
                                && StringUtils.equals("POST", httpServletRequest.getMethod())
                    );
        }
    }*/

}
