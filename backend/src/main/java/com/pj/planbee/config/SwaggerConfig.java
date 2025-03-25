package com.pj.planbee.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
                .select()
                // Controller가 위치한 패키지로 수정
                .apis(RequestHandlerSelectors.basePackage("com.pj.planbee.controller"))
                .paths(PathSelectors.any())
                .build();
    }
	
	private ApiInfo apiDetails() {
        return new ApiInfo(
                "PlanBee API",      // API 제목
                "PlanBee Project API Description", // API 설명
                "1.0",                   // 버전
                "Terms of Service URL",
                new Contact("PlanBEE", "https://github.com/kaereun3305/PlanBee.git", "PlanBEE@"),
                "License",
                "http://localhost:8080/PlanBEE",
                new ArrayList<>()
                
        );
    }
}

