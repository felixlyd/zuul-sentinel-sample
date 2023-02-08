package com.example.felixlyd.springcloudtemplate.config;

import com.example.felixlyd.springcloudtemplate.config.filter.AdapterPostFilter;
import com.example.felixlyd.springcloudtemplate.config.filter.AdapterPreFilter;
import com.netflix.zuul.ZuulFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * class classname
 *
 * @author : liuyaodong
 * @date 2023/2/7
 */
@Configuration
public class ZuulConfig {

    @Bean
    @Primary
    public ZuulFilter adapterPreFilter() {
        // We can also provider the filter order in the constructor.
        return new AdapterPreFilter();
    }

    @Bean
    @Primary
    public ZuulFilter adapterPostFilter() {
        return new AdapterPostFilter();
    }
}
