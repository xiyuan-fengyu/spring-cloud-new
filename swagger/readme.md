# swagger
参考 https://blog.csdn.net/wan_ide/article/details/105230307  
通过注解自动生成接口Api说明文档  
在 test/pom.xml 中添加依赖  
```
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
```

通过nacos web ui添加配置    
```
swagger.enable=true
```

为项目添加swagger配置  
test/src/main/java/com.xiyuan.cloud.config.SwaggerConfig  

在 com.xiyuan.cloud.controller.TestController 中添加注解    

访问 http://localhost:8080/swagger-ui.html 查看api doc  
ui的连接格式：http://ip:port/contextPath/swagger-ui.html  

正式环境通过设置  
```
swagger.enable=false
```
来禁用swagger  

