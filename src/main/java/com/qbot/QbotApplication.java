package com.qbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
public class QbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(QbotApplication.class, args);
    }

}
