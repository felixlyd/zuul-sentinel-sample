package com.example.felixlyd.springcloudtemplate.controller;

import com.example.felixlyd.springcloudtemplate.feign.TestFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试feign
 *
 * @author : liuyaodong
 * @date 2023/2/7
 */
@RestController
@Slf4j
@RequestMapping("/rule-controller2")
public class Test2Controller {

    @Autowired
    private TestFeignClient testFeignClient;

    @PostMapping("/**")
    public String test2(HttpServletRequest request, @RequestBody Map<String, String> reqMap){
        return testFeignClient.ruleFlow(reqMap);
    }
}
