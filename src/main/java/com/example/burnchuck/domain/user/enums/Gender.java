package com.example.burnchuck.domain.user.enums;

import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

    MALE("남", false),
    FEMALE("여", true);

    private String kor;
    private boolean value;

    public static Gender findEnum(String kor) {
        return Arrays.stream(values())
            .filter(gender -> gender.kor.equals(kor))
            .findFirst()
            .orElseThrow(() ->
                new CustomException(ErrorCode.INVALID_GENDER_FORMAT)
            );
    }
}
