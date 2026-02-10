package com.dacon.sideai.detection.service;

import com.dacon.sideai.detection.config.DetectionProperties;
import com.dacon.sideai.detection.domain.DetectionDto.AnalyzeRequest;
import com.dacon.sideai.detection.domain.DetectionDto.AnalyzeResponse;
import com.dacon.sideai.detection.domain.DetectionDto.ModelRequest;
import com.dacon.sideai.detection.domain.DetectionDto.ModelResponse;
import com.dacon.sideai.detection.domain.DetectionDto.ModelRiskLevel;
import com.dacon.sideai.detection.domain.DetectionDto.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class DetectionService {

  private final RestClient restClient;
  private final DetectionProperties properties;

  public DetectionService(DetectionProperties properties) {
    this.properties = properties;

    // 타임아웃 설정을 위한 RequestFactory
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout((int) properties.modelServer().connectTimeout());
    factory.setReadTimeout((int) properties.modelServer().readTimeout());

    this.restClient = RestClient.builder()
        .baseUrl(properties.modelServer().url())
        .requestFactory(factory)
        .build();
  }

  public AnalyzeResponse analyzeMessage(AnalyzeRequest request) {

    try {
      ModelRequest modelRequest = new ModelRequest(
          request.uuid(),
          request.messages(),
          request.platform(),
          request.type()
      );

      ModelResponse modelResponse = restClient.post()
          .uri(properties.modelServer().endpoint())
          .contentType(MediaType.APPLICATION_JSON)
          .body(modelRequest)
          .retrieve()
          .body(ModelResponse.class);

      return interpretResult(modelResponse);

    } catch (Exception e) {
      log.error("AI 모델 서버 통신 중 오류 발생: {}", e.getMessage(), e);
      // 클라이언트가 500 에러 대신 "분석 불가" 상태를 알 수 있도록 UNKNOWN 반환
      return new AnalyzeResponse(RiskLevel.UNKNOWN, "분석 서버 연결 실패: 잠시 후 다시 시도해주세요.", null, null,
          null, null, null);
    }
  }

  private AnalyzeResponse interpretResult(ModelResponse response) {
    if (response == null) {
      return new AnalyzeResponse(RiskLevel.UNKNOWN, "분석 결과가 비어있습니다.", null, null, null, null, null);
    }

    RiskLevel riskLevel = RiskLevel.UNKNOWN;
    ModelRiskLevel modelRiskLevel = response.riskLevel();
    if (modelRiskLevel != null) {
      riskLevel = switch (modelRiskLevel) {
        case CRITICAL -> RiskLevel.CRITICAL;
        case SUSPICIOUS -> RiskLevel.SUSPICIOUS;
        case NORMAL -> RiskLevel.NORMAL;
      };
    }

    return new AnalyzeResponse(
        riskLevel,
        response.summary(),
        response.type(),
        response.riskSignals(),
        response.recommendedQuestions(),
        response.additionalRecommendations(),
        response.ragReferences()
    );
  }
}
