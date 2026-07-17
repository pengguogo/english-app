package com.englishapp;

import com.englishapp.config.VoiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(VoiceProperties.class)
public class EnglishAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnglishAppApplication.class, args);
    }
}
