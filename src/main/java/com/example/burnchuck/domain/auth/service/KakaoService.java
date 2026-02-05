package com.example.burnchuck.domain.auth.service;

import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.auth.dto.response.KakaoUserInfoResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoService {

    private String clientId = "a851efa587488fee7e6a0a3508581f32";
    private String redirectUri = "http://localhost:8080/api/auth/kakao";

    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token",
                    new HttpEntity<>(params, headers),
                    Map.class
            );

            return (String) response.getBody().get("access_token");

        } catch (HttpClientErrorException e) {

            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfoResponse.class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {

            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }
}