package com.example.burnchuck.domain.review.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

    private Long meetingId;
    private Long reviewerId;
    private Long revieweeId;
    private Long rating;
    private List<Long> reactionList;
    private String detailedReview;


}
