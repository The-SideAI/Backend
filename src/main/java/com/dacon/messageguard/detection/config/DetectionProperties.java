package com.dacon.messageguard.detection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "detection")
public record DetectionProperties(
    Threshold threshold,
    ModelServer modelServer
) {

  public record Threshold(
      double warning,
      double critical
  ) {

  }

  public record ModelServer(
      String url,
      String endpoint,
      long connectTimeout,
      long readTimeout
  ) {

  }
}
