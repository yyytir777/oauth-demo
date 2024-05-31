package com.example.oauthdemo.config;

import com.example.oauthdemo.interceptor.AuthenticationInterceptor;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.http.HttpResponse;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
//    private final AuthenticationInterceptor authenticationInterceptor;
//    private final AdminAuthorizationInterceptor adminAuthorizationInterceptor;
//    private final MemberInfoArgumentResolver memberInfoArgumentResolver;

    //다른 오리진, 즉 포트가 다른 프로그램의 요청을 허용한다.어떤 경로에 대해서 설정할 것인지.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("Authorization", "Content-Type")
                .exposedHeaders("Custom-Header")
                .allowCredentials(true)
                .maxAge(3600);
    }
    //인터셉터 설정, 인증을 위한 인터셉터 설정을 override한다.

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/content");
    }
}