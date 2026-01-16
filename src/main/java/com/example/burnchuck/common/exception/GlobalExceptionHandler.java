package com.example.burnchuck.common.exception;

import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Void>> customException(CustomException e) {

        ErrorCode errorCode = e.getErrorCode();

        CommonResponse<Void> response = CommonResponse.exception(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // Valid 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        CommonResponse<Void> response = CommonResponse.exception(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
