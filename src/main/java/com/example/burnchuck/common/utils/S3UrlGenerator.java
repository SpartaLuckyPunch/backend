package com.example.burnchuck.common.utils;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.burnchuck.common.dto.GetS3Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class S3UrlGenerator {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    /**
     * 이미지 업로드용 Presigned URL 생성
     */
    public GetS3Url generateUploadImgUrl(String key) {
        Date expiration = getExpiration();
        GeneratePresignedUrlRequest request = createPresignedUrlRequest(key, HttpMethod.PUT, expiration);
        URL url = amazonS3Client.generatePresignedUrl(request);

        return GetS3Url.builder()
                .preSignedUrl(url.toExternalForm())
                .key(key)
                .build();
    }

    /**
     * 이미지 조회용 CloudFront 링크 생성
     */
    public GetS3Url generateViewImgUrl(String key) {
        String publicUrl = cloudFrontDomain + "/" + key;

        return GetS3Url.builder()
                .preSignedUrl(publicUrl)
                .key(key)
                .build();
    }

    /**
     * Presigned URL 기본 생성 Method
     */
    private GeneratePresignedUrlRequest createPresignedUrlRequest(String key, HttpMethod method, Date expiration) {
        return new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(method)
                .withExpiration(expiration);
    }

    /**
     * 만료 시간 지정
     */
    private Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60; // 1시간으로 설정하기
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
