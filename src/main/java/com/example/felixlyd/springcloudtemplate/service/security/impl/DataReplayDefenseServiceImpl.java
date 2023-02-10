package com.example.felixlyd.springcloudtemplate.service.security.impl;

import com.example.felixlyd.springcloudtemplate.service.security.DataReplayDefenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * class DataReplayDefenseServiceImpl
 * 数据防重放攻击实现类
 *
 * @author : liuyaodong
 * @date 2023/2/10
 */
@Service
@Slf4j
public class DataReplayDefenseServiceImpl implements DataReplayDefenseService {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 判断随机字符串是否合法
     *
     * @param nonce 随机字符串
     * @return 是否合法
     */
    @Override
    public boolean isNonceLegal(String nonce) {
        // 1. 从缓存管理器中获取nonce缓存
        Cache nonceCache = cacheManager.getCache("nonceCache");
        // 2. 缓存不存在，nonce直接不合法
        if(nonceCache ==null){
            log.error("nonceCache缓存不存在，请检查！");
            return false;
        }
        // 3. 尝试获取nonce的值
        Cache.ValueWrapper wrapper = nonceCache.get(nonce);
        // 3.1 缓存中没有nonce的值，nonce合法，放入缓存中
        if(wrapper==null){
            nonceCache.put(nonce, nonce);
            return true;
        // 3.2 缓存中存在nonce的值，nonce不合法
        }else {
            return false;
        }
    }

    /**
     * 判断时间戳是否合法
     *
     * @param timestamp 时间戳
     * @return 是否合法
     */
    @Override
    public boolean isTimeStampLegal(Long timestamp) {
        // 1. 转换时间戳为instant (jdk8新特性，纳秒级时间)
        Instant time;
        try{
            time = new Date(timestamp).toInstant();
        }catch (Exception e){
            // 转换失败，时间戳不合法
            log.error("检查时间戳！" + timestamp);
            return false;
        }
        // 2. 获取当前时间
        Instant sysTime = Instant.now();
        // 3. 时间比较差值，获取绝对值
        long timeDiff = Duration.between(time, sysTime).abs().getSeconds();
        // 4. 相差60s以内，合法，否则，不合法
        return timeDiff <= 60L;
    }

    /**
     * 根据时间戳+随机字符串判断是否重放攻击
     *
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @return 是否不为数据重放
     */
    @Override
    public boolean isNotDataReplay(Long timestamp, String nonce) {
        // 时间戳和随机字符串均为合法值时，不为数据重放，返回真
        return isTimeStampLegal(timestamp) && isNonceLegal(nonce);
    }
}
