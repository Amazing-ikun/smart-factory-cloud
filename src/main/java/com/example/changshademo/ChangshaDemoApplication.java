package com.example.changshademo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.example.changshademo.mapper")
public class ChangshaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChangshaDemoApplication.class, args);
    }

}
