package com.dacon.messageguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MessageGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageGuardApplication.class, args);
    }

}
