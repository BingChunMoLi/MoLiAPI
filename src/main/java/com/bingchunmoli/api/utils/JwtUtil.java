package com.bingchunmoli.api.utils;

import cn.hutool.crypto.KeyUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author moli
 */
@Slf4j
public class JwtUtil {

    private static final JWTSigner signer = JWTSignerUtil.createSigner("RS256", KeyUtil.generateKey("RS256", 2048));

    public static String createToken(Map<String, Object> payload){
        return JWTUtil.createToken(payload, signer);
    }

    public static JWT parseToken(String token){
        return JWTUtil.verify(token, signer) ? JWTUtil.parseToken(token): null;
    }
}
