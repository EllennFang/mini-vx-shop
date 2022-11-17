package com.powernode.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@EnableConfigurationProperties(SwaggerProperties.class)
@Configuration
@EnableOpenApi // 开启swagger功能
public class SwaggerAutoConfiguration {

    @Autowired
    private SwaggerProperties swaggerProperties;

    /**
     * 创建swagger文档
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)//指定文档版本
                .apiInfo(getApiInfo())//获取接口文档基本信息
                .select()
                //指定扫描包创建接口文档
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .build();
    }

    //读取文档基本信息
    private ApiInfo getApiInfo() {
        Contact contact = new Contact(swaggerProperties.getName(), swaggerProperties.getUrl(), swaggerProperties.getEmail());
        return new ApiInfoBuilder().
                contact(contact).
                title(swaggerProperties.getTitle()).
                description(swaggerProperties.getDescription()).
                termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl()).
                license(swaggerProperties.getLicense()).
                licenseUrl(swaggerProperties.getLicenseUrl()).
                build();
    }
}