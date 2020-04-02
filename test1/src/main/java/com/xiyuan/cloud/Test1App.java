package com.xiyuan.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created by xiyuan_fengyu on 2020/3/26 14:58.
 */
@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
public class Test1App {

    public static void main(String[] args) {
        SpringApplication.run(Test1App.class, args);
    }

}
