package com.example.felixlyd.springcloudtemplate.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 *
 * @author : liuyaodong
 * @date 2023/2/10
 */
@Configuration
public class CacheConfig {

    /**
     * 配置缓存管理器
     *
     * @return 缓存管理器
     */

    @Bean("cacheManager")
    public CacheManager cacheManager(@Qualifier("caffeineBuilder")
                                                         Caffeine<Object, Object> caffeineBuilder){
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(
                "nonceCache"
                );
        caffeineCacheManager.setCaffeine(caffeineBuilder);
        return caffeineCacheManager;
    }


    @Bean("caffeineBuilder")
    public Caffeine<Object, Object> caffeineBuilder(){
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(90, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000);
    }

}
