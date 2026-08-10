package com.usthe.bootshiro.config;

import com.usthe.bootshiro.util.JsonWebTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 签名密钥注入（P0 安全加固）
 * 密钥来源优先级：环境变量 HXAPI_JWT_SECRET > 系统属性 hxapigate.jwt.secret > 代码内开发兜底值
 * Spring 启动时通过 setter 注入将配置值写入 JsonWebTokenUtil，与网关核心保持同一密钥。
 */
@Configuration
public class JwtSecretConfiguration {

    @Value("${hxapigate.jwt.secret:}")
    public void setJwtSecret(String jwtSecret) {
        if (jwtSecret != null && !jwtSecret.isBlank()) {
            JsonWebTokenUtil.setSecretKey(jwtSecret);
        }
    }
}
