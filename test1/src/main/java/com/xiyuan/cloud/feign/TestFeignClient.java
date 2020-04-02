package com.xiyuan.cloud.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by xiyuan_fengyu on 2020/3/26 16:57.
 */
@FeignClient(name = "test", fallback = TestFeignClient.Fallback.class)
public interface TestFeignClient {

    @RequestMapping("/test")
    String test();

    @Configuration
    class Fallback implements TestFeignClient {

        @Override
        public String test() {
            return "服务暂不可用，请稍后重试";
        }

    }

}
