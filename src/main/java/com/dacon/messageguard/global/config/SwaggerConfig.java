package com.dacon.messageguard.global.config;

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
            .title("Message Guard API")
            .description("Chrome Extension - Phishing Detection Service API")
            .version("v1.0.0"));
  }
}
