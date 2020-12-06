package com.degombo.videostore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class VideoStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoStoreApplication.class, args);
    }

}
