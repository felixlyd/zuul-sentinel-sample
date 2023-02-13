package com.example.felixlyd.springcloudtemplate.service.security;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * class SmService
 *
 * @author : liuyaodong
 * @date 2023/2/10
 */
@Service
public interface SmService {
    /**
     * sm4算法加密 编码基于Base64
     * @param originStr 原始字符串
     * @return 加密的字符串
     */
    String sm4Encrypt(String originStr);

    /**
     * sm4算法解密 需Base64解码
     * @param encryptStr 加密的字符串
     * @return 解密的字符串
     */
    String sm4Decrypt(String encryptStr);

    /**
     * sm4算法加密 编码基于Base64(urlSafe)
     * @param originStr 原始字符串
     * @return 加密的字符串
     */
    String sm4EncryptUrlSafe(String originStr);

    /**
     * sm4算法解密 需Base64解码(urlSafe)
     * @param encryptStr 加密的字符串
     * @return 解密的字符串
     */
    String sm4DecryptUrlSafe(String encryptStr);

    /**
     * sm2验签 需Base64解码
     * @param digest 摘要
     * @param sign 签名
     * @return 是否验证通过
     */
    boolean sm2VerifySign(String digest, String sign);

    /**
     * sm2验签 需Base64解码
     * @param requestMap 请求报文
     * @param sign 签名
     * @return 是否验证通过
     */
    boolean sm2VerifySign(TreeMap<String, Object> requestMap, String sign);

    /**
     * sm2验签 需Base64解码
     * @param requestMap 请求报文
     * @param sign 签名
     * @return 是否验证通过
     */
    boolean sm2VerifySign(HashMap<String, Object> requestMap, String sign);

    /**
     * sm2签名 编码基于Base64
     * @param originStr 原始字符串
     * @return 签名
     */
    String sm2Sign(String originStr);

    /**
     * sm2签名 编码基于Base64
     * @param map hashMap格式的报文
     * @return 签名
     */
    String sm2Sign(HashMap<String, Object> map);

    /**
     * sm2签名 编码基于Base64
     * @param map treeMap格式的报文
     * @return 签名
     */
    String sm2Sign(TreeMap<String, Object> map);

    /**
     * sm3摘要加密 编码基于Base64
     * @param originStr 原始字符串
     * @return 摘要
     */
    String sm3Encrypt(String originStr);

    /**
     * sm3摘要加密 编码基于Base64
     * @param requestMap 请求报文
     * @return 摘要
     */
    String sm3Encrypt(TreeMap<String, Object> requestMap);

    /**
     * sm3摘要加密 编码基于Base64
     * @param requestMap 请求报文
     * @return 摘要
     */
    String sm3Encrypt(HashMap<String, Object> requestMap);

    /**
     * 拼接请求报文
     * @param requestMap 请求报文
     * @return 拼接报文的字符串
     */
    String joinRequestMap(TreeMap<String, Object> requestMap);

    /**
     * 拼接请求报文
     * @param requestMap 请求报文
     * @return 拼接报文的字符串
     */
    String joinRequestMap(HashMap<String, Object> requestMap);
}
