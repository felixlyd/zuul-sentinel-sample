package com.example.felixlyd.springcloudtemplate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * class classname
 *
 * @author : liuyaodong
 * @date 2023/2/7
 */
@Controller
@Slf4j
@RequestMapping("/rule-controller")
public class TestController {

    @PostMapping("/**")
    public String test2(HttpServletRequest request){
        log.info("协议转换--");
        String path = request.getServletPath();
        List<String> paths = Arrays.stream(path.split("/")).filter(i -> !i.contains("rule-controller")).collect(Collectors.toList());
        String zuulPath = "/rule" + String.join("/", paths);
        log.info(zuulPath);
        return zuulPath;
    }
}
