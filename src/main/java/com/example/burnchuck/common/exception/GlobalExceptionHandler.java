package com.example.burnchuck.common.exception;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.enums.ErrorCode;
import jakarta.servlet.ServletException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j(topic = "GlobalExceptionHandler")
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Void>> customException(CustomException e) {

        ErrorCode errorCode = e.getErrorCode();

        CommonResponse<Void> response = CommonResponse.exception(errorCode.getMessage());

        log.warn("CustomException 발생 : {}", errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // Valid 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        CommonResponse<Void> response = CommonResponse.exception(message);

        log.warn("MethodArgumentNotValidException 발생 : {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // HTTP 요청 본문(Body)을 자바 객체로 변환하는 과정이 실패했을 때(ex: JSON 파싱 오류, 요청 본문 누락)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> httpMessageNotReadableException(HttpMessageNotReadableException e) {

        CommonResponse<Void> response = CommonResponse.exception("요청 본문 형식이 올바르지 않습니다");

        log.warn("HttpMessageNotReadableException 발생 : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 지원하지 않는 HTTP 메서드로 요청했을 때
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {

        CommonResponse<Void> response = CommonResponse.exception("지원하지 않는 HTTP 메서드입니다.");

        log.warn("HttpRequestMethodNotSupportedException 발생 : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    // 요청 데이터의 타입과 실제 매개변수의 타입이 일치하지 않을 때
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {

        String message = String.format("파라미터 '%s'의 값이 올바르지 않습니다.", e.getName());
        CommonResponse<Void> response = CommonResponse.exception(message);

        log.warn("MethodArgumentTypeMismatchException 발생 : {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ServletException을 상속받는 모든 예외 처리(요청 형식이 잘못되었을 때)
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<CommonResponse<Void>> exception(ServletException e) {

        CommonResponse<Void> response = CommonResponse.exception("요청 형식이 올바르지 않습니다.");

        log.warn("ServletException 발생 : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 외부 시스템 / 비동기 처리 시 Timeout 되었을 때
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<CommonResponse<Void>> handleTimeout(TimeoutException e) {

        CommonResponse<Void> response = CommonResponse.exception("요청 시간이 초과되었습니다.");

        log.error("TimeoutException 발생", e);

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
    }

    // 외부 API 호출 실패
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<CommonResponse<Void>> handleHttpClientError(HttpClientErrorException e) {

        CommonResponse<Void> response = CommonResponse.exception("외부 서비스 요청이 실패했습니다.");

        log.error("HttpClientErrorException 발생", e);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    // RuntimeException을 상속받는 모든 예외 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<Void>> handleRuntimeException(RuntimeException e) {

        CommonResponse<Void> response = CommonResponse.exception("서버 내부 오류가 발생했습니다.");

        log.error("기타 예외 발생", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
