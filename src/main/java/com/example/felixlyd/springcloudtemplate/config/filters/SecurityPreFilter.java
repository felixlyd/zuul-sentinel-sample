package com.example.felixlyd.springcloudtemplate.config.filters;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.example.felixlyd.springcloudtemplate.service.security.DataReplayDefenseService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 解密请求报文、验签、防重放
 *
 * @author : liuyaodong
 * @date 2023/2/7
 */
@Slf4j
@Configuration
public class SecurityPreFilter extends ZuulFilter {

    @Autowired
    private DataReplayDefenseService dataReplayDefenseService;

    @Value("${security-filter.pre:20000}")
    private int preFilterOrder;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return preFilterOrder;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 1. 获取Request对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request =requestContext.getRequest();
        String routeId = (String)requestContext.get("proxy");
        try{
            if(StringUtil.equals(routeId,"a-server")){
                log.info("解密--");
                Map<String , String > map = new HashMap<>(5);
                map.put("nonce", "5");
                map.put("timestamp",String.valueOf(System.currentTimeMillis()));
                boolean status = dataReplayDefenseService.isNonceLegal(map.get("nonce"));
                boolean status2 = dataReplayDefenseService.isTimeStampLegal(Long.valueOf(map.get("timestamp")));
                log.info(String.valueOf(status));
            }

        }catch (Exception e){
            ZuulBlockFallbackProvider zuulBlockFallbackProvider = ZuulBlockFallbackManager.getFallbackProvider(routeId);
            BlockResponse blockResponse = zuulBlockFallbackProvider.fallbackResponse(routeId, e);
            requestContext.setRouteHost((URL)null);
            requestContext.set("serviceId", (Object)null);
            requestContext.setResponseBody(blockResponse.toString());
            requestContext.setResponseStatusCode(blockResponse.getCode());
            requestContext.getResponse().setContentType("application/json; charset=utf-8");
        }
        return null;
    }
}
