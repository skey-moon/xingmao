package com.xingmao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xingmao.mapper")
public class XingMaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(XingMaoApplication.class, args);
    }
}