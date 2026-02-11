package com.example.burnchuck.common.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    @Setter
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}
