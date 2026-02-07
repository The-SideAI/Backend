package com.dacon.sideai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SideAiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SideAiApplication.class, args);
  }

}
