package com.example.felixlyd.springcloudtemplate.config.filters;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletResponse;

/**
 * 加密响应报文
 *
 * @author : liuyaodong
 * @date 2023/2/7
 */
@Slf4j
@Configuration
public class SecurityPostFilter extends ZuulFilter {

    @Value("${security-filter.post:500}")
    private int postFilterOrder;
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return postFilterOrder;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        // 1. 获取Request对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse request =requestContext.getResponse();
        String routeId = (String)requestContext.get("proxy");
        if(StringUtil.equals(routeId,"a-server")){
            log.info("加密--");
        }
        return null;
    }


}
