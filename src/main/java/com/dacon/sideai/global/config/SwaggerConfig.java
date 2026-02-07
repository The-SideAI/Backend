package com.dacon.sideai.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI messageGuardOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Side ai API")
            .description("Chrome Extension - Phishing Detection Service API")
            .version("v1.0.0"));
  }
}
