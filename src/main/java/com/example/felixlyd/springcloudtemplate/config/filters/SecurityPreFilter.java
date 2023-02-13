package com.example.felixlyd.springcloudtemplate.config.filters;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.example.felixlyd.springcloudtemplate.service.security.DataReplayDefenseService;
import com.example.felixlyd.springcloudtemplate.service.security.SmService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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

    @Autowired
    private ObjectMapper objectMapper;

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
        // 1. 获取请求上下文
        RequestContext requestContext = RequestContext.getCurrentContext();
        String routeId = (String)requestContext.get("proxy");
        try{
            if(StringUtil.equals(routeId,"a-server")){
                HashMap<String , Object > map = new HashMap<>(5);
                map.put("nonce", "5");
                map.put("timestamp",String.valueOf(System.currentTimeMillis()));
                String sign = smService.sm2Sign(map);
                map.put("sign", sign);
                String sm4StrE = smService.sm4EncryptUrlSafe(objectMapper.writeValueAsString(map));
                log.info("解密--");
                HttpServletRequest request =requestContext.getRequest();
                String encryptRequest = request.getParameter("request");
                String originRequest = smService.sm4DecryptUrlSafe(encryptRequest);
                HashMap<String , Object > requestMap = objectMapper.readValue(originRequest, new TypeReference<HashMap<String, Object>>(){});
                log.info("数据防重放验证--");
                if(requestMap.containsKey("timestamp")&&requestMap.containsKey("nonce")){
                    boolean isLegal = dataReplayDefenseService.isNotDataReplay(Long.valueOf((String) requestMap.get("timestamp")),
                            (String) requestMap.get("nonce"));
                    if(!isLegal){
                        throw new AccessControlException("该数据已经处理过，请勿提交重复数据！1");
                    }
                }else {
                    throw new NoSuchElementException("请求报文中缺少timestamp和nonce！");
                }
                log.info("签名验证--");
                if(requestMap.containsKey("sign")){
                    boolean isSignOk = smService.sm2VerifySign(requestMap, (String) requestMap.get("sign"));
                    if(!isSignOk){
                        throw new AccessControlException("数据签名不正确！");
                    }
                }else {
                    throw new NoSuchElementException("请求报文中缺少sign！");
                }
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
