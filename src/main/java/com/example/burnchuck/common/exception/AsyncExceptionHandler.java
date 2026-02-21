package com.example.burnchuck.common.exception;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

@Slf4j(topic = "AsyncExceptionHandler")
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.warn("비동기 처리 예외 발생. method={}, message={}", method.getName(), ex.getMessage());
    }
}
