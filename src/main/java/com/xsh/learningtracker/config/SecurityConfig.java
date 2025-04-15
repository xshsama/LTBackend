package com.xsh.learningtracker.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final JwtAuthFilter jwtAuthFilter; // 添加JWT过滤器

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder); // Use injected PasswordEncoder
        return authProvider;
    }

    // Removed passwordEncoder() Bean definition

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                // 在UsernamePasswordAuthenticationFilter之前添加JWT过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 允许访问/api/auth/**端点
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 允许OPTIONS请求用于CORS预检
                        // 添加个人资料API的安全配置 - 修改为permitAll()让未设置用户信息时不会报错
                        .requestMatchers(HttpMethod.GET, "/api/profile").authenticated() // 获取个人资料需要认证
                        .requestMatchers(HttpMethod.PUT, "/api/profile").authenticated() // 更新个人资料需要认证
                        .requestMatchers(HttpMethod.POST, "/api/avatar/upload/**").authenticated() // 上传图片需要认证
                        .requestMatchers(HttpMethod.POST, "/api/user/password").authenticated() // 修改密码需要认证
                        .requestMatchers("/api/user/preferences/**").authenticated() // 用户偏好设置相关的所有请求都需要认证
                        .requestMatchers(HttpMethod.GET, "/api/subjects/**").permitAll() // 允许获取学习主题
                        .requestMatchers(HttpMethod.POST, "/api/subjects/**").authenticated() // 修改学习主题需要认证
                        .requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll() // 允许获取标签
                        .requestMatchers(HttpMethod.POST, "/api/tags/**").authenticated() // 修改标签需要认证
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll() // 允许获取分类
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").authenticated() // 修改分类需要认证
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }
}
