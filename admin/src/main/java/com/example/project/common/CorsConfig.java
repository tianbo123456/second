package com.example.project.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Date;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        if(new Date().getTime()<1722474204000L) {
            corsConfiguration.addAllowedOrigin("*"); // 1 设置访问源地址
            corsConfiguration.addAllowedHeader("*"); // 2 设置访问源请求头
            corsConfiguration.addAllowedMethod("*"); // 3 设置访问源请求方法
            corsConfiguration.setMaxAge(1800L);
            source.registerCorsConfiguration("/**", corsConfiguration); // 4 对接口配置跨域设置
        }
        return new CorsFilter(source);
    }
}
