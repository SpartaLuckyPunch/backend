package com.example.burnchuck.common.enums;

import com.example.burnchuck.common.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE("남", false),
    FEMALE("여", true);

    private final String kor;
    private final boolean value;

    public static Gender findEnum(String kor) {
        return Arrays.stream(values())
            .filter(gender -> gender.kor.equals(kor))
            .findFirst()
            .orElseThrow(() ->
                new CustomException(ErrorCode.INVALID_GENDER_FORMAT)
            );
    }
}
