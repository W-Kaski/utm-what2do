package com.utm.what2do.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CORS配置属性
 */
@Data
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 允许的源
     */
    private List<String> allowedOrigins;

    /**
     * 允许的HTTP方法
     */
    private List<String> allowedMethods;

    /**
     * 允许的请求头
     */
    private List<String> allowedHeaders;

    /**
     * 是否允许携带凭证
     */
    private boolean allowCredentials = true;
}
