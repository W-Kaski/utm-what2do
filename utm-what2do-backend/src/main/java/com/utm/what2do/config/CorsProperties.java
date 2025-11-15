package com.utm.what2do.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * Origins allowed to access the API.
     */
    private List<String> allowedOrigins = List.of();

    /**
     * HTTP methods permitted in CORS preflight requests.
     */
    private List<String> allowedMethods = List.of();

    /**
     * Headers permitted in CORS preflight requests.
     */
    private List<String> allowedHeaders = List.of();

    /**
     * Whether browser credentials (cookies, authorization headers) are allowed.
     */
    private boolean allowCredentials = true;
}
