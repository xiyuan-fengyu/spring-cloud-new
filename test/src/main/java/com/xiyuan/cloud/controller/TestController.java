package com.xiyuan.cloud.controller;

import com.xiyuan.cloud.config.TestConfig;
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
    private TestConfig testConfig;

    @ResponseBody
    @RequestMapping("/test")
    public String test() {
        return testConfig.toString();
    }

}
