package com.utm.what2do;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.utm.what2do.mapper")
public class UtmWhat2DoBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(UtmWhat2DoBackendApplication.class, args);
    }
}
