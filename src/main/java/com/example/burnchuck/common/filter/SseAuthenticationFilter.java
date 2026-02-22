package com.example.burnchuck.common.filter;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.UserRole;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.jwt.JwtAuthenticationToken;
import com.example.burnchuck.common.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class SseAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        if (acceptHeader != null && acceptHeader.contains("text/event-stream")) {

            String jwt = null;
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for(Cookie cookie : cookies) {
                    if("accessToken".equals(cookie.getName())) {
                        jwt= cookie.getValue();
                        break;
                    }
                }
            }

            if (jwt == null || !jwtUtil.validateToken(jwt)) {
                handleCustomException(response, new CustomException(ErrorCode.INVALID_TOKEN));
                return;
            }

            Long userId = jwtUtil.extractId(jwt);
            String email = jwtUtil.extractEmail(jwt);
            String nickname = jwtUtil.extractNickname(jwt);
            String roleString = jwtUtil.extractRole(jwt);
            UserRole userRole = UserRole.of(roleString);

            AuthUser authUser = new AuthUser(userId, email, nickname, userRole);
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private void handleCustomException(HttpServletResponse response, CustomException e) throws IOException {

        response.setStatus(e.getErrorCode().getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CommonResponse commonResponse = CommonResponse.exception(e.getErrorCode().getMessage());
        writeErrorResponse(response, commonResponse);
    }

    private void writeErrorResponse(HttpServletResponse response, CommonResponse body) throws IOException {

        String json = objectMapper.writeValueAsString(body);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush();
        }
    }
}
