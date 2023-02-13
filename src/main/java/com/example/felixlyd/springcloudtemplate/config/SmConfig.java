package com.example.felixlyd.springcloudtemplate.config;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.symmetric.SM4;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * class classname
 *
 * @author : liuyaodong
 * @date 2023/2/10
 */
@Configuration
public class SmConfig {
    @Value("${sm.sm4key}")
    private String sm4Key;

    @Value("${sm.sm2key-public}")
    private String sm2PublicKey;

    @Value("${sm.sm2key-private}")
    private String sm2PrivateKey;

    @Bean("sm4")
    public SM4 sm4(){
        byte[] sm4KeyBytes = Base64.getDecoder().decode(sm4Key.getBytes(StandardCharsets.UTF_8));
        return new SM4(Mode.CFB, Padding.NoPadding, sm4KeyBytes, sm4KeyBytes);
    }

    @Bean("sm2")
    public SM2 sm2(){
        byte[] sm2PrivateKeyBytes = Base64.getDecoder().decode(sm2PrivateKey.getBytes(StandardCharsets.UTF_8));
        byte[] sm2PublicKeyBytes = Base64.getDecoder().decode(sm2PublicKey.getBytes(StandardCharsets.UTF_8));
        SM2 sm2 = new SM2(sm2PrivateKeyBytes, sm2PublicKeyBytes);
        sm2.setMode(SM2Engine.Mode.C1C3C2);
        return sm2;
    }

    @Bean("sm3")
    public SM3 sm3(){
        return new SM3();
    }
}
