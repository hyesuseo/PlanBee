package com.pj.planbee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
        .addPathPatterns("/**") // 모든 요청 가로채기
        .excludePathPatterns(
            "/auth/**",
            "/css/**", "/js/**", "/images/**", "/favicon.ico", // 정적 리소스들
            "/swagger-ui.html",          // Swagger UI 기본 페이지
            "/swagger-resources/**",     // Swagger 관련 리소스들
            "/v2/api-docs",              // Swagger API 문서
            "/webjars/**"                // Swagger 웹자원들
        );
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://planbee-two.vercel.app") // FE 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "X-Requested-With", "Accept", "Authorization")
                .allowCredentials(true);
    }
}
