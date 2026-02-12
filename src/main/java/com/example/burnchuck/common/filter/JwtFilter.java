package com.example.burnchuck.common.filter;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.UserRole;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.jwt.JwtAuthenticationToken;
import com.example.burnchuck.common.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

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

        // 토큰이 없는 경우 처리
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.validateToken(jwt)) {
            handleCustomException(response, new CustomException(ErrorCode.INVALID_TOKEN));
            return;
        }

        // refresh 토큰으로 접근하는 경우
        if (jwtUtil.isRefreshToken(jwt)) {
            handleCustomException(response, new CustomException(ErrorCode.INVALID_TOKEN));
            return;
        }

        // 토큰 저장
        Long userId = jwtUtil.extractId(jwt);
        String email = jwtUtil.extractEmail(jwt);
        String nickname = jwtUtil.extractNickname(jwt);
        String roleString = jwtUtil.extractRole(jwt);
        UserRole userRole = UserRole.of(roleString);

        AuthUser authUser = new AuthUser(userId, email, nickname, userRole);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    // Filter 내부에서 발생하는 CustomException 처리
    private void handleCustomException(HttpServletResponse response, CustomException e) throws IOException {

        response.setStatus(e.getErrorCode().getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CommonResponse commonResponse = CommonResponse.exception(e.getErrorCode().getMessage());
        writeErrorResponse(response, commonResponse);
    }

    private void writeErrorResponse(HttpServletResponse response, CommonResponse body) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = objectMapper.writeValueAsString(body);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush();
        }
    }
}
