package com.example.felixlyd.springcloudtemplate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

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

/*    @PostMapping("/{path1}")
    public String test(@PathVariable String path1){
        log.info("/rule/" + path1);
        return "/rule/" + path1;
    }

    @PostMapping("/{path1}/{path2}")
    public String test2(@PathVariable String path1, @PathVariable String path2){
        log.info("/rule/" + path1 + "/" + path2);
        return "/rule/" + path1 + "/" + path2;
    }*/

    @PostMapping("/**")
    public String test2(HttpServletRequest request){
        String path = request.getServletPath();
        List<String> paths = Arrays.stream(path.split("/")).filter(i -> !i.contains("rule-controller")).collect(Collectors.toList());
        String zuulPath = "/rule/" + String.join("/", paths);
        log.info(zuulPath);
        return zuulPath;
    }
}
