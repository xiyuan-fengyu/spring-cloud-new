package com.xiyuan.cloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.xiyuan.cloud.feign.TestFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xiyuan_fengyu on 2020/3/26 15:01.
 */
@Controller
public class TestController {

    @Autowired
    private TestFeignClient testFeignClient;

    @ResponseBody
    @RequestMapping("/test")
    @SentinelResource(value = "test", blockHandler = "testBlocked")
    public String test(Long ms) {
        return testFeignClient.testSleep(ms);
    }

    // testBlocked 相比于原方法 test，参数列表多了一个 BlockException e 参数
    public String testBlocked(Long ms, BlockException e) {
        if (e instanceof FlowException) {
            return "请求太过频繁，请稍后再试";
        }
        else if (e instanceof DegradeException) {
            return "服务不可用，请稍后再试";
        }
        return "请稍后再试";
    }

}
