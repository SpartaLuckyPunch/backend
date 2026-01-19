package com.example.burnchuck.domain.review.model.request;

import com.example.burnchuck.common.enums.ValidationMessage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

    @NotNull(message = ValidationMessage.ID_NOT_NULL)
    private Long meetingId;

    @Min(value = 1, message = ValidationMessage.RATING_MIN)
    @Max(value = 5, message = ValidationMessage.RATING_MAX)
    @NotNull(message = ValidationMessage.RATING_NOT_NULL)
    private Long rating;

    private List<Long> reactionList;

    private String detailedReview;


}
