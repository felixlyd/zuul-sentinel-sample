package com.example.felixlyd.springcloudtemplate.config.filters;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.example.felixlyd.springcloudtemplate.service.security.DataReplayDefenseService;
import com.example.felixlyd.springcloudtemplate.service.security.SmService;
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

    @Autowired
    private SmService smService;

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
                HashMap<String , Object > map = new HashMap<>(5);
                map.put("nonce", "5");
                map.put("timestamp",String.valueOf(System.currentTimeMillis()));
                boolean status = dataReplayDefenseService.isNonceLegal((String) map.get("nonce"));
                boolean status2 = dataReplayDefenseService.isTimeStampLegal(Long.valueOf((String) map.get("timestamp")));
                log.info(String.valueOf(status));
                String originStr = smService.joinRequestMap(map);
                String sm4StrE = smService.sm4Encrypt(originStr);
                String sm4StrD = smService.sm4Decrypt(sm4StrE);
                String digest = smService.sm3Encrypt(originStr);
                String digest2 = smService.sm3Encrypt(map);
                String sign = smService.sm2Sign(originStr);
                String sign2 = smService.sm2Sign(map);
                boolean ok = smService.sm2VerifySign(digest, sign);
                boolean ok2 = smService.sm2VerifySign(map, sign2);
                log.info(String.valueOf(ok2));
            }

        }catch (Exception e){
            ZuulBlockFallbackProvider zuulBlockFallbackProvider = ZuulBlockFallbackManager.getFallbackProvider(routeId);
            BlockResponse blockResponse = zuulBlockFallbackProvider.fallbackResponse(routeId, e);
            log.error(e.getMessage(), e.getCause());
            requestContext.setRouteHost((URL)null);
            requestContext.set("serviceId", (Object)null);
            requestContext.setResponseBody(blockResponse.toString());
            requestContext.setResponseStatusCode(blockResponse.getCode());
            requestContext.getResponse().setContentType("application/json; charset=utf-8");
        }
        return null;
    }
}
