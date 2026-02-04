package com.example.burnchuck.common.utils;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.burnchuck.common.dto.GetS3Url;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class S3UrlGenerator {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    // 허용된 파일 확장자
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png"
    );

    /**
     * 이미지 업로드용 Presigned URL 생성
     */
    public GetS3Url generateUploadImgUrl(String filename, String key) {

        if (!validateFileType(filename)) {
            throw new CustomException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }

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
     * S3에 이미지 파일 존재 여부 확인
     */
    public boolean isFileExists(String key) {
        try {
            amazonS3Client.getObjectMetadata(bucket, key);
            return true;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    /**
     * Key 소유권 검증
     */
    public void validateKeyOwnership(Long userId, String key) {
        if (!key.startsWith("profile/" + userId + "/")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_IMAGE_ACCESS);
        }
    }

    /**
     * 파일 형식 검증
     */
    private boolean validateFileType(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        String extension = filename.substring(lastDotIndex + 1).toLowerCase();

        return ALLOWED_EXTENSIONS.contains(extension);
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
        expTimeMillis += 1000 * 60 * 10; // 10분으로 설정하기
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
