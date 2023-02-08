package com.example.felixlyd.springcloudtemplate.config.filter;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPostFilter;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;

/**
 * class classname
 *
 * @author : liuyaodong
 * @date 2023/2/7
 */
@Slf4j
public class AdapterPostFilter extends SentinelZuulPostFilter {
    @Override
    public Object run() throws ZuulException {
        super.run();
        log.info("--加密响应报文中--");
        return null;
    }
}
