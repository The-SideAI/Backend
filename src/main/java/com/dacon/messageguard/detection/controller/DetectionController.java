package com.dacon.messageguard.detection.controller;

import com.dacon.messageguard.detection.domain.DetectionDto.*;
import com.dacon.messageguard.detection.service.DetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Detection", description = "피싱 탐지 및 분석 API")
@RestController
@RequestMapping("/api/detection")
@RequiredArgsConstructor
// 크롬 익스텐션 개발 중에는 모든 출처 허용, 배포 시 익스텐션 ID로 제한 권장
@CrossOrigin(origins = "*")
public class DetectionController {

    private final DetectionService detectionService;

    @Operation(summary = "메시지 분석 요청", description = "텍스트를 받아 AI 모델을 통해 피싱 여부를 판단합니다.")
    @PostMapping("/analyze")
    public AnalyzeResponse analyze(@RequestBody @Valid AnalyzeRequest request) {
        return detectionService.analyzeMessage(request);
    }

    // 서버 생존 확인용
    @Operation(summary = "헬스 체크", description = "서버가 정상 작동 중인지 확인합니다.")
    @GetMapping("/health")
    public String healthCheck() {
        return "Message Guard API is running.";
    }
}
