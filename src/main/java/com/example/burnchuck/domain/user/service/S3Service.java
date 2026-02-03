package com.example.burnchuck.domain.user.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.GetS3Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 프로필 이미지 수정 (post)
     */
    @Transactional(readOnly = true)
    public GetS3Url getPostS3Url(AuthUser authUser, String filename) {

        String key = "profile/" + authUser.getId() + "/" + UUID.randomUUID() + "/" + filename;

        Date expiration = getExpiration();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = getPostGeneratePresignedUrlRequest(key, expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return GetS3Url.builder()
                .preSignedUrl(url.toExternalForm())
                .key(key)
                .build();
    }

    /**
     * 프로필 이미지 수정 (get)
     */
    @Transactional(readOnly = true)
    public GetS3Url getGetS3Url(String key) {

        Date expiration = getExpiration();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGetGeneratePresignedUrlRequest(key, expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return GetS3Url.builder()
                .preSignedUrl(url.toExternalForm())
                .key(key)
                .build();
    }

    private GeneratePresignedUrlRequest getPostGeneratePresignedUrlRequest(String fileName, Date expiration) {
        return new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withKey(fileName)
                .withExpiration(expiration);
    }

    private GeneratePresignedUrlRequest getGetGeneratePresignedUrlRequest(String key, Date expiration) {
        return new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
    }

    private static Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60; // 1시간으로 설정하기
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
