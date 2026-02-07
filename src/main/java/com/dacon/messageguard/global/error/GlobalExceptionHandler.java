package com.dacon.messageguard.global.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 1. 잘못된 인자값 전달 (400 Bad Request)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e,
      HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
  }

  // 2. DTO 유효성 검사 실패 (@Valid) (400 Bad Request)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e,
      HttpServletRequest request) {
    String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
  }

  // 3. 외부 모델 서버 통신 오류 (503 Service Unavailable)
  @ExceptionHandler({ResourceAccessException.class, RestClientException.class})
  public ResponseEntity<ErrorResponse> handleNetworkException(Exception e,
      HttpServletRequest request) {
    return buildResponse(
        HttpStatus.SERVICE_UNAVAILABLE,
        "AI 모델 서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.",
        request
    );
  }

  // 4. 그 외 모든 서버 내부 에러 (500 Internal Server Error)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception e,
      HttpServletRequest request) {
    e.printStackTrace(); // 서버 로그에는 남김
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
        request
    );
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message,
      HttpServletRequest request) {
    ErrorResponse response = ErrorResponse.of(
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(response);
  }
}
