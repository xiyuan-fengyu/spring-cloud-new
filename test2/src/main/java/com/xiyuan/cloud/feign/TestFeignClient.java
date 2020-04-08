package com.xiyuan.cloud.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by xiyuan_fengyu on 2020/3/26 16:57.
 */
@FeignClient(name = "test", fallback = TestFeignClient.Fallback.class)
public interface TestFeignClient {

    @RequestMapping(value = "/testSleep", method = RequestMethod.GET)
    String testSleep(@RequestParam("ms") Long ms);

    @Configuration
    class Fallback implements TestFeignClient {

        @Override
        public String testSleep(Long ms) {
            return "服务暂不可用，请稍后重试";
        }

    }

}
