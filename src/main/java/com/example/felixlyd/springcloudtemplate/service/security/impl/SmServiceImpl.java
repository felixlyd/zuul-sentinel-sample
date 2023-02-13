package com.example.felixlyd.springcloudtemplate.service.security.impl;

import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.symmetric.SM4;
import com.example.felixlyd.springcloudtemplate.service.security.SmService;
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * class classname
 *
 * @author : liuyaodong
 * @date 2023/2/10
 */
@Service
public class SmServiceImpl implements SmService {

    @Autowired
    @Qualifier("sm4")
    private SM4 sm4;

    @Autowired
    @Qualifier("sm2")
    private SM2 sm2;

    @Autowired
    @Qualifier("sm3")
    private SM3 sm3;

    /**
     * sm4算法加密 编码基于Base64
     *
     * @param originStr 加密的字符串
     * @return 解密的字符串
     */
    @Override
    public String sm4Encrypt(String originStr) {
        byte[] encryptBytes = sm4.encrypt(originStr.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(encryptBytes), StandardCharsets.UTF_8);
    }

    /**
     * sm4算法解密 需Base64解码
     * @param encryptStr 加密的字符串
     * @return 解密的字符串
     */
    @Override
    public String sm4Decrypt(String encryptStr) {
        byte[] encryptBytes = Base64.getDecoder().decode(encryptStr.getBytes(StandardCharsets.UTF_8));
        byte[] originBytes = sm4.decrypt(encryptBytes);
        return new String(originBytes, StandardCharsets.UTF_8);
    }

    /**
     * sm4算法加密 编码基于Base64(urlSafe)
     *
     * @param originStr 加密的字符串
     * @return 解密的字符串
     */
    @Override
    public String sm4EncryptUrlSafe(String originStr) {
        byte[] encryptBytes = sm4.encrypt(originStr.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getUrlEncoder().encode(encryptBytes), StandardCharsets.UTF_8);
    }

    /**
     * sm4算法解密 需Base64解码(urlSafe)
     * @param encryptStr 加密的字符串
     * @return 解密的字符串
     */
    @Override
    public String sm4DecryptUrlSafe(String encryptStr) {
        byte[] encryptBytes = Base64.getUrlDecoder().decode(encryptStr.getBytes(StandardCharsets.UTF_8));
        byte[] originBytes = sm4.decrypt(encryptBytes);
        return new String(originBytes, StandardCharsets.UTF_8);
    }

    /**
     * sm2验签 需Base64解码
     *
     * @param digest 摘要
     * @param sign   签名
     * @return 是否验证通过
     */
    @Override
    public boolean sm2VerifySign(String digest, String sign) {
        byte[] digestBytes = Base64.getDecoder().decode(digest.getBytes(StandardCharsets.UTF_8));
        byte[] signBytes = Base64.getDecoder().decode(sign.getBytes(StandardCharsets.UTF_8));
        return sm2.verify(digestBytes, signBytes);
    }

    /**
     * sm2验签 需Base64解码
     *
     * @param requestMap 请求报文
     * @param sign       签名
     * @return 是否验证通过
     */
    @Override
    public boolean sm2VerifySign(TreeMap<String, Object> requestMap, String sign) {
        String digest = sm3Encrypt(requestMap);
        return sm2VerifySign(digest, sign);
    }

    /**
     * sm2验签 需Base64解码
     *
     * @param requestMap 请求报文
     * @param sign       签名
     * @return 是否验证通过
     */
    @Override
    public boolean sm2VerifySign(HashMap<String, Object> requestMap, String sign) {
        String digest = sm3Encrypt(requestMap);
        return sm2VerifySign(digest, sign);
    }

    /**
     * sm2签名 编码基于Base64
     *
     * @param originStr 摘要
     * @return 签名
     */
    @Override
    public String sm2Sign(String originStr) {
        String digest = sm3Encrypt(originStr);
        byte[] digestBytes = Base64.getDecoder().decode(digest.getBytes(StandardCharsets.UTF_8));
        byte[] encryptDigestBytes = sm2.sign(digestBytes);
        String encryptBase64Digest = Base64.getEncoder().encodeToString(encryptDigestBytes);
        return new String(encryptBase64Digest.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * sm2签名 编码基于Base64
     *
     * @param map hashMap格式的报文
     * @return 签名
     */
    @Override
    public String sm2Sign(HashMap<String, Object> map) {
        String originStr = joinRequestMap(map);
        return sm2Sign(originStr);
    }

    /**
     * sm2签名 编码基于Base64
     *
     * @param map treeMap格式的报文
     * @return 签名
     */
    @Override
    public String sm2Sign(TreeMap<String, Object> map) {
        String originStr = joinRequestMap(map);
        return sm2Sign(originStr);
    }

    /**
     * sm3摘要加密 编码基于Base64
     *
     * @param originStr 原始字符串
     * @return 摘要
     */
    @Override
    public String sm3Encrypt(String originStr) {
        byte[] encryptBytes = sm3.digest(originStr.getBytes(StandardCharsets.UTF_8));
        String encryptStr =  Base64.getEncoder().encodeToString(encryptBytes);
        return new String(encryptStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * sm3摘要加密 编码基于Base64
     *
     * @param requestMap 请求报文
     * @return 摘要
     */
    @Override
    public String sm3Encrypt(TreeMap<String, Object> requestMap) {
        String originStr = joinRequestMap(requestMap);
        return sm3Encrypt(originStr);
    }

    /**
     * sm3摘要加密 编码基于Base64
     *
     * @param requestMap 请求报文
     * @return 摘要
     */
    @Override
    public String sm3Encrypt(HashMap<String, Object> requestMap) {
        String originStr = joinRequestMap(requestMap);
        return sm3Encrypt(originStr);
    }

    /**
     * 拼接请求报文
     *
     * @param requestMap 请求报文
     * @return 拼接报文的字符串
     */
    @Override
    public String joinRequestMap(TreeMap<String, Object> requestMap) {
        requestMap.remove("sign");
        return Joiner.on("&").withKeyValueSeparator("=").join(requestMap);
    }

    /**
     * 拼接请求报文
     *
     * @param requestMap 请求报文
     * @return 拼接报文的字符串
     */
    @Override
    public String joinRequestMap(HashMap<String, Object> requestMap) {
        TreeMap<String, Object> treeMap = new TreeMap<>(requestMap);
        return joinRequestMap(treeMap);
    }
}
