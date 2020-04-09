# Sentinel  
提供限流熔断功能，可以和 nacos 一起使用  

# 集成过程
须先完成 nacos 的集成，查看 nacos/readme.md     

## 限流
参考 https://blog.csdn.net/autfish/article/details/90405679   
在 test2/pom.xml 中添加依赖  
```
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    <version>2.2.0.RELEASE</version>
</dependency>

<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
    <version>1.7.2</version>
</dependency>
```  

修改 test2 的 nacos 配置   
```
spring.cloud.sentinel.datasource.flow.nacos.serverAddr=${spring.cloud.nacos.config.server-addr}
spring.cloud.sentinel.datasource.flow.nacos.groupId=${spring.cloud.nacos.config.group}
spring.cloud.sentinel.datasource.flow.nacos.dataId=${spring.application.name}-sentinel-flow
spring.cloud.sentinel.datasource.flow.nacos.ruleType=flow
``` 

为test2项目添加一个限流的 nacos 配置 data-id: test2-sentinel-flow, group: test     
```json
[
  {
    "resource": "test",
    "controlBehavior": 2,
    "count": 20,
    "grade": 1,
    "limitApp": "default",
    "strategy": 0
  }
]
```

修改 test2/src/main/java/com/xiyuan/cloud/controller/TestController.java 实现限流  
```
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
```

@SentinelResource 的 value 一定要填写，且不要和 RequestMapping的value一样，否则 testBlocked 会几率性失效，原因未知  

## 熔断
修改 test2 的 nacos 配置，添加一下配置     
```
feign.sentinel.enabled=true
ribbon.ConnectTimeout=3000
ribbon.ReadTimeout=10000

spring.cloud.sentinel.datasource.degrade.nacos.serverAddr=${spring.cloud.nacos.config.server-addr}
spring.cloud.sentinel.datasource.degrade.nacos.groupId=${spring.cloud.nacos.config.group}
spring.cloud.sentinel.datasource.degrade.nacos.dataId=${spring.application.name}-sentinel-degrade
spring.cloud.sentinel.datasource.degrade.nacos.ruleType=degrade
``` 

为test2项目添加一个熔断的 nacos 配置 data-id: test2-sentinel-degrade, group: test     
```json
[
  {
    "resource": "test",
    "count": 500,
    "grade": 0,
    "timeWindow": 10
  }
]
```

由于 sentinel 和 openfeign 的版本问题， com.alibaba.cloud.sentinel.feign.SentinelContractHolder 中的一个方法
parseAndValidatateMetadata， 在 feign.Contract 中叫做 parseAndValidateMetadata，导致方法找不到，所以我们自己编写一个
com.alibaba.cloud.sentinel.feign.SentinelContractHolder 来覆盖jar中的class    
test2/src/main/java/com/alibaba/cloud/sentinel/feign/SentinelContractHolder.java  
```java
/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.sentinel.feign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feign.Contract;
import feign.MethodMetadata;

/**
 *
 * Using static field {@link SentinelContractHolder#METADATA_MAP} to hold
 * {@link MethodMetadata} data.
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class SentinelContractHolder implements Contract {

    private final Contract delegate;

    /**
     * map key is constructed by ClassFullName + configKey. configKey is constructed by
     * {@link feign.Feign#configKey}
     */
    public final static Map<String, MethodMetadata> METADATA_MAP = new HashMap<>();

    public SentinelContractHolder(Contract delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
        List<MethodMetadata> metadatas = delegate.parseAndValidateMetadata(targetType);
        metadatas.forEach(metadata -> METADATA_MAP
                .put(targetType.getName() + metadata.configKey(), metadata));
        return metadatas;
    }

//    @Override
//    public List<MethodMetadata> parseAndValidatateMetadata(Class<?> targetType) {
//        List<MethodMetadata> metadatas = delegate.parseAndValidatateMetadata(targetType);
//        metadatas.forEach(metadata -> METADATA_MAP
//                .put(targetType.getName() + metadata.configKey(), metadata));
//        return metadatas;
//    }

}
```
修改 test 项目，增加一个接口，让请求根据传递的参数sleep一段时间，模拟卡顿    
test/src/main/java/com/xiyuan/cloud/controller/TestController.java
```
    @ResponseBody
    @RequestMapping(value = "/testSleep", method = RequestMethod.GET)
    public String testSleep(Long ms) throws InterruptedException {
        if (ms != null && ms > 0) {
            Thread.sleep(ms);
        }
        return "sleep: " + ms;
    }
```
在 test2 增加一个feign client  
```
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
```

启动 test 和 test2   
运行 test2/src/main/java/com/xiyuan/cloud/FlowDegradeTest.java 来测试限流和熔断效果  
运气好的话，就能看到类似下面的提示：  
```
req-36: 服务不可用，请稍后再试
req-16: sleep: 1000
req-48: 请求太过频繁，请稍后再试
req-37: 服务不可用，请稍后再试
req-17: sleep: 1000
```
有正常完成的请求，有被限流的，也有被熔断的  

## sentinel-dashboard
https://github.com/alibaba/Sentinel/tree/master/sentinel-dashboard  
参考 https://blog.51cto.com/zero01/2425570  
在 https://github.com/alibaba/Sentinel/releases 下载 sentinel-dashboard-1.7.2.jar    
在命令行运行一下命令启动    
```
java -Dserver.port=9091 -Dcsp.sentinel.dashboard.server=localhost:9091 -Dproject.name=sentinel-dashboard -Dsentinel.dashboard.auth.username=sentinel -Dsentinel.dashboard.auth.password=123456 -jar sentinel-dashboard-1.7.2.jar
```

修改 test2 nacos 配置，使其连入 dashboard  
```
management.endpoints.web.exposure.include=sentinel
spring.cloud.sentinel.eager=true
spring.cloud.sentinel.transport.dashboard=localhost:9091
```
另外 sentinel-transport-*-1.7.1.jar 中用到了 com.alibaba.csp.sentinel.log.CommandCenterLog 这个类，
但这个类在 sentinel-core-1.7.2.jar 中没有了  
我们自己定义一个  
test2/src/main/java/com/alibaba/csp/sentinel/log/CommandCenterLog.java
```
package com.alibaba.csp.sentinel.log;

public class CommandCenterLog extends com.alibaba.csp.sentinel.log.LogBase {

    public static void	info(String detail, Object... params) {}

    public static void	info(String detail, Throwable e) {}

    public static void	warn(String detail, Object... params) {}

    public static void	warn(String detail, Throwable e) {}

}
```
重启 test2 后  
浏览器访问(用户名密码在启动命令中可配置)  
http://localhost:9091        
便可以在 sentinel-dashboard 中看到 test2 的限流熔断信息      
可以看到三个资源   
```
GET:http://test/testSleep
test
/test
```
test 资源是通过 @SentinelResource 定义的，可以触发 testBlocked 回调    
/test是sentinel 通过 com.xiyuan.cloud.controller.TestController 中的@RequestMapping自动生成的，不能触发回调    
GET:http://test/testSleep 是通过 com.xiyuan.cloud.feign.TestFeignClient 中的@RequestMapping自动生成的，不能触发回调  
