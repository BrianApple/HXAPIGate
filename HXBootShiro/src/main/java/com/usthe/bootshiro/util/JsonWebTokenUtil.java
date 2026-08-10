package com.usthe.bootshiro.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usthe.bootshiro.domain.vo.JwtAccount;
import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * jwt签发解析工具类:
 * @author tomsun28
 * @date 16:29 2018/3/8
 */
public class JsonWebTokenUtil {

    /**
     * JWT 签名密钥（P0 安全加固：不再硬编码固定值）
     * 优先取环境变量 HXAPI_JWT_SECRET / 系统属性 hxapigate.jwt.secret，
     * 由 JwtSecretConfiguration 在启动时注入；下方为纯开发环境兜底值，
     * 生产环境必须通过 HXAPI_JWT_SECRET 提供强随机密钥。
     */
    public static volatile String SECRET_KEY = "HXAPIGate_DEV_ONLY_DEFAULT_SECRET_9daf6b06758fcb0259a2eb0bf24879a0";

    /** 启动时注入 JWT 密钥（Spring 调用） */
    public static void setSecretKey(String secret) {
        if (secret != null && !secret.isBlank()) {
            SECRET_KEY = secret;
        }
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int COUNT_2 = 2;

    private JsonWebTokenUtil() {

    }

    /**
     * 根据密钥字符串生成 HMAC 密钥对象（jjwt 0.12+ 要求 SecretKey）
     * @param key 密钥字符串
     * @return javax.crypto.SecretKey
     */
    public static SecretKey generateKey(String key) {
        try {
            // 将任意长度密钥通过 SHA-512 固定为 512bit，满足 HS512 签名要求（HS256/384 亦兼容）
            // 注意：原实现用 SHA-256(256bit)，jjwt 0.12+ 对 HS512 校验密钥强度时报 WeakKeyException
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 算法不可用", e);
        }
    }

    /**
     *   json web token 签发
     * @param id 令牌ID
     * @param subject 用户ID
     * @param issuer 签发人
     * @param period 有效时间(秒)
     * @param roles 访问主张-角色
     * @param permissions 访问主张-权限
     * @param algorithm 加密算法
     * @return java.lang.String
     */
    public static String issueJWT(String id,String subject, String issuer, Long period, String roles, String permissions, SignatureAlgorithm algorithm) {
        // 当前时间戳
        Long currentTimeMillis = System.currentTimeMillis();
        JwtBuilder jwtBuilder = Jwts.builder();
        if (!StringUtils.isEmpty(id)) {
            jwtBuilder.id(id);
        }
        if (!StringUtils.isEmpty(subject)) {
            jwtBuilder.subject(subject);
        }
        if (!StringUtils.isEmpty(issuer)) {
            jwtBuilder.issuer(issuer);
        }
        // 设置签发时间
        jwtBuilder.issuedAt(new Date(currentTimeMillis));
        // 设置到期时间
        if (null != period) {
            jwtBuilder.expiration(new Date(currentTimeMillis+period*1000));
        }
        if (!StringUtils.isEmpty(roles)) {
            jwtBuilder.claim("roles",roles);
        }
        if (!StringUtils.isEmpty(permissions)) {
            jwtBuilder.claim("perms",permissions);
        }
        // 加密设置（jjwt 0.12+ 使用 SecretKey 签名）
        jwtBuilder.signWith(generateKey(SECRET_KEY), algorithm);

        return jwtBuilder.compact();
    }

    /**
     * 解析JWT的Payload
     */
    public static String parseJwtPayload(String jwt){
        Assert.hasText(jwt, "JWT String argument cannot be null or empty.");
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            String msg = "JWT strings must contain exactly 2 period characters. Found: " + (parts.length - 1);
            throw new MalformedJwtException(msg);
        }
        // Base64 URL 解码 payload（第2段）
        byte[] payloadBytes = java.util.Base64.getUrlDecoder().decode(parts[1]);
        return new String(payloadBytes, StandardCharsets.UTF_8);
    }

    /**
     * 验签JWT
     *
     * @param jwt json web token
     */
    public static JwtAccount parseJwt(String jwt, String appKey) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(generateKey(appKey))
                .build()
                .parseSignedClaims(jwt);
        Claims claims = jws.getPayload();
        JwtAccount jwtAccount = new JwtAccount();
        //令牌ID
        jwtAccount.setTokenId(claims.getId());
        // 客户标识
        jwtAccount.setAppId(claims.getSubject());
        // 签发者
        jwtAccount.setIssuer(claims.getIssuer());
        // 签发时间
        jwtAccount.setIssuedAt(claims.getIssuedAt());
        // 接收方
        jwtAccount.setAudience(claims.getAudience() == null ? null : String.join(",", claims.getAudience()));
        // 访问主张-角色
        jwtAccount.setRoles(claims.get("roles", String.class));
        // 访问主张-权限
        jwtAccount.setPerms(claims.get("perms", String.class));
        return jwtAccount;
    }


    /**
     * description 从json数据中读取格式化map
     *
     * @param val 1
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readValue(String val) {
        try {
            return MAPPER.readValue(val, Map.class);
        } catch (IOException e) {
            throw new MalformedJwtException("Unable to read JSON value: " + val, e);
        }
    }

    /**
     * 按","分割字符串进SET
     */
    @SuppressWarnings("unchecked")
    public static Set<String> split(String str) {

        Set<String> set = new HashSet<>();
        if (StringUtils.isEmpty(str)) {
            return set;
        }
        set.addAll(Arrays.asList(str.split(",")));
        return set;
    }

}
