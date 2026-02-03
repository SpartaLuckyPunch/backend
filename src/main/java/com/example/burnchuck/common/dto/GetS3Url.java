package com.example.burnchuck.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class GetS3Url {

    private String preSignedUrl;

    private String key;

    @Builder
    public GetS3Url(String preSignedUrl, String key) {
        this.preSignedUrl = preSignedUrl;
        this.key = key;
    }
}