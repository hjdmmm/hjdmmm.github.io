package com.hjdmmm.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class HjdmmmBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(HjdmmmBlogApplication.class, args);
    }
}
