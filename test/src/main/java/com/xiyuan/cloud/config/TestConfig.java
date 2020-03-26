package com.xiyuan.cloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiyuan_fengyu on 2020/3/26 15:44.
 */
@Configuration
@ConfigurationProperties(prefix = "test-config")
public class TestConfig {

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestConfig{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
