package com.example.felixlyd.springcloudtemplate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新发起http请求通过zuul转发
 *
 * @author : liuyaodong
 * @date 2023/2/7
 */
@RestController
@Slf4j
@RequestMapping("/rule-controller3")
public class NewHttpTestController {

    @PostMapping("/**")
    public String test2(HttpServletRequest request, @RequestBody Map<String, String> reqMap){
        log.info("解密--");
        String path = request.getServletPath();
        List<String> paths = Arrays.stream(path.split("/")).filter(i -> !i.contains("rule-controller")).collect(Collectors.toList());
        String zuulPath = "http://localhost:8081/rule" + String.join("/", paths);
        RestTemplate restTemplate = new RestTemplate();
        String responseBody = restTemplate.postForObject(zuulPath, reqMap, String.class);
        log.info("加密--");
        return responseBody;
    }
}
