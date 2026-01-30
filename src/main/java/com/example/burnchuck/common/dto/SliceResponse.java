package com.example.burnchuck.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SliceResponse<T> {

    private final List<T> content;
    private final int size;
    private final int number;
    private final boolean hasNext;

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(
                slice.getContent(),
                slice.getSize(),
                slice.getNumber(),
                slice.hasNext()
        );
    }
}
