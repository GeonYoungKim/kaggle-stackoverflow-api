package com.skuniv.cs.geonyeong.kaggleapi;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class KaggleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaggleApiApplication.class, args);
    }
}
