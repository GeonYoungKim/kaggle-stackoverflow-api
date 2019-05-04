package com.skuniv.cs.geonyeong.kaggleapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class KaggleApiConfiguration {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("kaggle stackoverflow")
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.skuniv.cs.geonyeong.kaggleapi.controller"))
                .paths(PathSelectors.ant("/api/v1/kaggle/stackoverflow/**"))
                .build();
    }
}
