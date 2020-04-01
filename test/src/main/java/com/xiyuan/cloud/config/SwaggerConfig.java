package com.xiyuan.cloud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * Created by xiyuan_fengyu on 2020/4/1 17:32.
 */
@Configuration
@ConfigurationProperties(prefix = "swagger")
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean enable;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfo(
                        "test api documentation",
                        "swagger demo",
                        "0.0.1",
                        "https://github.com/xiyuan-fengyu/spring-cloud-new",
                        new Contact("xiyuan_fengyu", "https://github.com/xiyuan-fengyu", "xiyuan_fengyu@163.com"),
                        "Apache 2.0",
                        "http://www.apache.org/licenses/LICENSE-2.0",
                        new ArrayList<>()
                ))
                .enable(enable)
                .select()
                //RequestHandlerSelectors 配置要扫描接口的方式
                //basePackage：指定要扫描的包
                //any()：描述全部
                //none()：不扫描
                //withClassAnnotation：扫描类上的注解，参数是一个注解的反射对象
                //withMethodAnnotation：描述方法的注解
                //.apis(RequestHandlerSelectors.withMethodAnnotation(RequestMapping.class))
                .apis(RequestHandlerSelectors.basePackage("com.xiyuan.cloud.controller"))
                //paths() 过滤扫描路径
                //.paths(PathSelectors.ant("/lyr/**"))
                .build();
    }

}
