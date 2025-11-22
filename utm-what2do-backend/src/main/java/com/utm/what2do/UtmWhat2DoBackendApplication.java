package com.utm.what2do;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.utm.what2do.mapper")
@EnableCaching
@EnableScheduling
public class UtmWhat2DoBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(UtmWhat2DoBackendApplication.class, args);
    }
}
