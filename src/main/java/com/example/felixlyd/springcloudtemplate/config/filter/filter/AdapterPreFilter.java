package com.example.felixlyd.springcloudtemplate.config.filter.filter;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPreFilter;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;

/**
 * class classname
 *
 * @author : liuyaodong
 * @date 2023/2/7
 */
@Slf4j
public class AdapterPreFilter extends SentinelZuulPreFilter {
    @Override
    public Object run() throws ZuulException {
        super.run();
        log.info("--解密请求报文中--");
        return null;
    }
}
