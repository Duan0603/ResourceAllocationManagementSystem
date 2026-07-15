package com.company.resourcealloc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ResourceAllocApplication {
    public static void main(String[] args) {
        // Force JVM timezone to UTC to avoid invalid TimeZone "Asia/Saigon" PSQLException in Windows environment
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(ResourceAllocApplication.class, args);
    }
}
