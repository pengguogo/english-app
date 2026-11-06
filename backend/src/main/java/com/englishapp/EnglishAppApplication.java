package com.englishapp;

import com.englishapp.config.VoiceProperties;
import com.englishapp.config.WordImageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({VoiceProperties.class, WordImageProperties.class})
public class EnglishAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnglishAppApplication.class, args);
    }
}
