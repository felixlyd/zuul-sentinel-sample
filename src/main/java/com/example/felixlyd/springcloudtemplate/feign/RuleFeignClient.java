package com.example.felixlyd.springcloudtemplate.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * class classname
 *
 * @author : liuyaodong
 * @date 2023/2/8
 */
@FeignClient("rule-liteflow")
public interface RuleFeignClient {
    @PostMapping("/api-a/rule")
    String ruleFlow(@RequestBody Map<String, String> reqMap);
}
