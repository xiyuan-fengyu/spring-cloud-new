package com.xiyuan.cloud.controller;

import com.xiyuan.cloud.config.TestConfig;
import com.xiyuan.cloud.param.Message;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xiyuan_fengyu on 2020/3/26 15:01.
 */
@Controller
public class TestController {

    @Autowired
    private TestConfig testConfig;

    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation(value = "测试swagger", httpMethod = "GET")
    public String test(@ApiParam("测试实体类参数") Message message) {
        return message + "\n" + testConfig.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/testSleep", method = RequestMethod.GET)
    public String testSleep(Long ms) throws InterruptedException {
        if (ms != null && ms > 0) {
            Thread.sleep(ms);
        }
        return "sleep: " + ms;
    }

}
