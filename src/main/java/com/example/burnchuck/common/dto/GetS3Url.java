package com.example.burnchuck.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetS3Url {

    private String preSignedUrl;

    private String cloudFrontUrl;

    private String key;

    @Builder
    public GetS3Url(String preSignedUrl, String cloudFrontUrl, String key) {
        this.preSignedUrl = preSignedUrl;
        this.cloudFrontUrl = cloudFrontUrl;
        this.key = key;
    }
}