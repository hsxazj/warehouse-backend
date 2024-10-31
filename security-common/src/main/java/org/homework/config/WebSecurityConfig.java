package org.homework.config;

import org.homework.exceptionhandler.SimpleAccessDeniedHandler;
import org.homework.exceptionhandler.SimpleAuthenticationEntryPoint;
import org.homework.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // 开启SpringSecurity的自动配置  如果是springboot项目则不需要此注解
// 新的方法安全开启方法
@EnableMethodSecurity(securedEnabled = false)
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 创建基于内存的用户信息管理器
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // 使用manager管理
        manager.createUser(
                // 创建UserDetailsService对象，用户管理用户名，密码，角色，权限等内容
                User.withDefaultPasswordEncoder().username("zhang").password("123456").roles("USER")
                        .build());
        return manager;
    }


    /**
     * 注册 密码编码/校验器 Bean
     *
     * @return BCryptPasswordEncoder 对象
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 新版本中，我们需要对authenticationManager进行额外的配置
     *
     * @param userDetailsService 自定义的 userDetailsService
     * @param passwordEncoder    密码编码/校验器 Bean
     * @return 授权管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(provider));
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /*
          配置信息
         */
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(authorize ->
                authorize
                        // TODO 这里有个小坑，有些前端同学会操作不规范，带着token访问登录接口，导致请求被拦截
                        //  实在解决不了的情况下可以把anonymous()改为permitAll()
                        .requestMatchers("/login").anonymous()
                        // TODO 在这里配置更多接口
                        .anyRequest().authenticated());


        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        /*
          添加跨域配置，新版本中我们不需要再通过继承WebMvcConfigurer的方式来配置跨域，在的lambda中配置即可
         */
        http.cors(cors -> {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOriginPattern("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("PUT,GET,POST,DELETE");
            source.registerCorsConfiguration("/**", config);
            cors.configurationSource(source);
        });

        /*
          添加自定义的授权和认证处理器
         */
        http.exceptionHandling(exh -> exh
                .authenticationEntryPoint(new SimpleAuthenticationEntryPoint())
                .accessDeniedHandler(new SimpleAccessDeniedHandler()));

        return http.build();
    }


}