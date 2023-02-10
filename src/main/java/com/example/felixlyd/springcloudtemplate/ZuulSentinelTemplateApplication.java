package com.example.felixlyd.springcloudtemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liuyaodong
 */
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableFeignClients
@EnableCaching
public class ZuulSentinelTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulSentinelTemplateApplication.class, args);
    }

}
