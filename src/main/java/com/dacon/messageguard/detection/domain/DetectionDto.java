package com.dacon.messageguard.detection.domain;

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
            String platform        // (옵션) 텍스트를 가져온 URL
    ) {}

    public record Message(
        MessageType type, // "TEXT" 또는 "URL"
        @NotEmpty(message = "메시지 내용은 비어있을 수 없습니다.")
        String content,   // 분석할 텍스트
        String sender,    // 발신자 정보
        @NotNull(message = "메시지 타임스탬프는 필수입니다.")
        Instant timestamp // 메시지 시각 (예: 2026-02-03T10:30:00Z)
    ) {}

    public enum MessageType {
        TEXT,
        URL
    }

    // 2. [Spring Boot -> Model Server] 요청
    public record ModelRequest(
            UUID uuid,              // 분석 요청 식별자
            List<Message> messages, // 대화 내역 리스트
            String platform        // (옵션) 텍스트를 가져온 URL
    ) {}

    // 3. [Model Server -> Spring Boot] 응답
    public record ModelResponse(
        @JsonProperty("UUID") UUID uuid,
        @JsonProperty("risk_stage")
        ModelRiskLevel riskLevel,
        String summary,
        String type,
        List<Reason> reason,
        @JsonProperty("recommended_questions")
        List<String> recommendedQuestions
    ) {}

    public record Reason(
        String source,
        @JsonProperty("note")
        String quote
    ) {}

    // 4. [Spring Boot -> Chrome Extension] 최종 응답
    public record AnalyzeResponse(
        RiskLevel riskLevel,    // 위험도: NORMAL, SUSPICIOUS, CRITICAL, UNKNOWN
        String summary,         // 종합 의견
        String type,            // 피싱 유형 (예: 중고거래, 대출권유 등)
        List<Reason> reason,    // 판단 근거 리스트
        List<String> recommendedQuestions // 사용자가 이어서 할 수 있는 질문 추천
    ) {}

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
