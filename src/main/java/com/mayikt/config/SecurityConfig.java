package com.mayikt.config;

import com.mayikt.entity.Permission;
import com.mayikt.mapper.PermissionMapper;
import com.mayikt.security.MyUserDetailService;
import com.mayikt.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mayikt.handler.MyAuthenticationFailureHandler;
import com.mayikt.handler.MyAuthenticationSuccessHandler;

import java.util.List;

// Security 配置
@Component
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyAuthenticationFailureHandler failureHandler;
    @Autowired
    private MyAuthenticationSuccessHandler successHandler;
    @Autowired
    private MyUserDetailService myUserDetailService;
    @Autowired
    private PermissionMapper permissionMapper;

    // 配置认证用户信息和权限
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		// 添加admin账号
//		auth.inMemoryAuthentication().withUser("admin").password("123456").
//		authorities("showOrder","addOrder","updateOrder","deleteOrder");
//		// 添加userAdd账号
//		auth.inMemoryAuthentication().withUser("userAdd").password("123456").authorities("showOrder","addOrder");
        // 如果想实现动态账号与数据库关联 在该地方改为查询数据库
        auth.userDetailsService(myUserDetailService).passwordEncoder(new PasswordEncoder() {
            //对表单传过来的值进行加密
            @Override
            public String encode(CharSequence charSequence) {
                return MD5Util.encode(charSequence + "");
            }

            //将加密后的密码和数据库里面做对比
            //charSequence：表单
            //s:数据库
            @Override
            public boolean matches(CharSequence charSequence, String s) {
                String charSequences = MD5Util.encode(charSequence + "");
                boolean equals = charSequences.equals(s);
                return equals;
            }
        });
    }

    // 配置拦截请求资源
    protected void configure(HttpSecurity http) throws Exception {
    	//死数据
        // 如何权限控制 给每一个请求路径 分配一个权限名称 让后账号只要关联该名称，就可以有访问权限
		//        http.authorizeRequests()
		//// 配置查询订单权限
		//.antMatchers("/showOrder").hasAnyAuthority("showOrder")
		//.antMatchers("/addOrder").hasAnyAuthority("addOrder")
		//.antMatchers("/login").permitAll()//放行登录页面
		//.antMatchers("/updateOrder").hasAnyAuthority("updateOrder")
		//.antMatchers("/deleteOrder").hasAnyAuthority("deleteOrder")

        //动态获取权限
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http.authorizeRequests();
        List<Permission> allPermission = permissionMapper.findAllPermission();
        for (Permission permission : allPermission) {
            expressionInterceptUrlRegistry.antMatchers(permission.getUrl()).hasAnyAuthority(permission.getPermTag());
        }
        expressionInterceptUrlRegistry
                .antMatchers("/login").permitAll()//放行登录页面
                .antMatchers("/**").fullyAuthenticated()
                .and().formLogin()//formLogin安全框架内置的登录页面
                .loginPage("/login")
                .successHandler(successHandler).failureHandler(failureHandler)//自定处理登录的handler
                .and().csrf().disable();//跨域请求伪造
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

}
