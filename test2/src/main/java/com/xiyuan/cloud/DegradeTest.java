package com.xiyuan.cloud;

import org.springframework.web.client.RestTemplate;

/**
 * Created by xiyuan_fengyu on 2020/4/8 15:51.
 */
public class DegradeTest {

    public static void main(String[] args) throws InterruptedException {
        long ms = 1000;
        long preReqNum = 10;
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < preReqNum; i++) {
            Thread thread = new Thread(() -> {
                String res = restTemplate.getForObject("http://localhost:8082/test?ms=" + ms, String.class);
                System.out.println(Thread.currentThread().getName() + ": " + res);
            });
            thread.setName("req-" + i);
            thread.start();
            Thread.sleep(500);
        }
        String res = restTemplate.getForObject("http://localhost:8082/test?ms=" + ms, String.class);
        System.out.println(Thread.currentThread().getName() + ": " + res);
    }

}