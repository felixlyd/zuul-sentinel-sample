package com.example.felixlyd.springcloudtemplate.service.security;

/**
 * class DataReplayDefenseService
 * 数据防重放接口
 *
 * @author : liuyaodong
 * @date 2023/2/10
 */
public interface DataReplayDefenseService {
    /**
     * 判断随机字符串是否合法
     * @param nonce 随机字符串
     * @return 是否合法
     */
    boolean isNonceLegal(String nonce);

    /**
     * 判断时间戳是否合法
     * @param timestamp 时间戳
     * @return 是否合法
     */
    boolean isTimeStampLegal(Long timestamp);

    /**
     * 根据时间戳+随机字符串判断是否重放攻击
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @return 是否不为数据重放
     */
    boolean isNotDataReplay(Long timestamp, String nonce);
}
