package com.example.burnchuck.common.utils;

import jakarta.servlet.http.HttpServletRequest;

public class ClientInfoExtractor {

    public static String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
