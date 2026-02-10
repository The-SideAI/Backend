package com.dacon.sideai.detection.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DetectionDto {

  // 1. [Chrome Extension -> Spring Boot] 요청
  public record AnalyzeRequest(
      UUID uuid,              // 분석 요청 식별자
      @NotNull(message = "메시지 리스트는 필수입니다.")
      List<Message> messages, // 대화 내역 리스트
      String platform,       // (옵션) 텍스트를 가져온 URL
      String type            // (옵션) 메시지 유형(예: 중고거래 등)
  ) {

  }

  public record Message(
      MessageType type, // "TEXT" 또는 "URL"
      @NotEmpty(message = "메시지 내용은 비어있을 수 없습니다.")
      String content,   // 분석할 텍스트
      String sender,    // 발신자 정보
      @NotNull(message = "메시지 타임스탬프는 필수입니다.")
      Instant timestamp // 메시지 시각 (예: 2026-02-03T10:30:00Z)
  ) {

  }

  public enum MessageType {
    TEXT,
    URL
  }

  // 2. [Spring Boot -> Model Server] 요청
  public record ModelRequest(
      UUID uuid,              // 분석 요청 식별자
      List<Message> messages, // 대화 내역 리스트
      String platform,       // (옵션) 텍스트를 가져온 URL
      String type            // (옵션) 메시지 유형(예: 중고거래 등)
  ) {

  }

  // 3. [Model Server -> Spring Boot] 응답
  public record ModelResponse(
      @JsonProperty("UUID") UUID uuid,
      @JsonProperty("risk_stage")
      ModelRiskLevel riskLevel,
      String summary,
      String type,
      @JsonProperty("risk_signals")
      List<RiskSignal> riskSignals,
      @JsonProperty("recommended_questions")
      List<String> recommendedQuestions,
      @JsonProperty("additional_recommendations")
      List<String> additionalRecommendations,
      @JsonProperty("rag_references")
      List<RagReference> ragReferences
  ) {

  }

  public record RiskSignal(
      String quote,
      String reason
  ) {

  }

  public record RagReference(
      String source,
      String summary
  ) {

  }

  // 4. [Spring Boot -> Chrome Extension] 최종 응답
  public record AnalyzeResponse(
      RiskLevel riskLevel,    // 위험도: NORMAL, SUSPICIOUS, CRITICAL, UNKNOWN
      String summary,         // 종합 의견
      String type,            // 피싱 유형 (예: 중고거래, 대출권유 등)
      @JsonProperty("risk_signals")
      List<RiskSignal> riskSignals,    // 판단 근거 리스트
      @JsonProperty("recommended_questions")
      List<String> recommendedQuestions, // 사용자에게 제안할 질문 리스트
      @JsonProperty("additional_recommendations")
      List<String> additionalRecommendations, // 추가 권고사항
      @JsonProperty("rag_references")
      List<RagReference> ragReferences // 참고한 자료 출처
  ) {

  }

  public enum RiskLevel {
    @JsonProperty("normal")
    NORMAL,
    @JsonProperty("suspicious")
    SUSPICIOUS,
    @JsonProperty("critical")
    CRITICAL,
    UNKNOWN
  }

  public enum ModelRiskLevel {
    @JsonProperty("normal")
    NORMAL,
    @JsonProperty("suspicious")
    SUSPICIOUS,
    @JsonProperty("critical")
    CRITICAL
  }
}
