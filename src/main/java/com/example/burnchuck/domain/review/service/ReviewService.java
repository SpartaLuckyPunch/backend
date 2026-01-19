package com.example.burnchuck.domain.review.service;

import com.example.burnchuck.common.entity.*;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.reaction.repository.ReactionRepository;
import com.example.burnchuck.domain.review.model.request.ReviewCreateRequest;
import com.example.burnchuck.domain.review.repository.ReviewReactionRepository;
import com.example.burnchuck.domain.review.repository.ReviewRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.burnchuck.common.enums.ErrorCode.SELF_REVIEW_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final MeetingRepository meetingRepository;
    private final ReactionRepository reactionRepository;
    private final ReviewReactionRepository reviewReactionRepository;

    /**
     * 후기 등록
     */
    @Transactional
    public void createReview(Long userId, Long revieweeId, ReviewCreateRequest request) {

        // 1. reviewer(리뷰 작성자)가 존재하는지 검증
        User reviewer = userRepository.findActivateUserById(userId);

        // 2. reviewee(리뷰 대상자)가 존재하는 검증
        User reviewee = userRepository.findActivateUserById(revieweeId);

        // 3. meeting이 존재하는 검증
        Meeting meeting = meetingRepository.findActivateMeetingById(request.getMeetingId());

        // 4. 중복 리뷰 검증
        if (reviewRepository.existsByMeetingIdAndReviewerIdAndRevieweeId(
                meeting.getId(), reviewer.getId(), reviewee.getId())) {
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);

        }

        // 5. 자기 자신에게 후기 작성 방지
        if (reviewer.getId().equals(reviewee.getId()))
            throw new CustomException(SELF_REVIEW_NOT_ALLOWED);

        // 6. Review 저장
        Review review = new Review(
                request.getRating().intValue(),
                request.getDetailedReview(),
                reviewer,
                reviewee,
                meeting
        );
        reviewRepository.save(review);

        // 7. ReviewReaction(반응) 중간 테이블 저장
        if (request.getReactionList() != null) {
            for (Long reactionId : request.getReactionList()) {
                Reaction reaction = reactionRepository.findReactionById(reactionId);
                reviewReactionRepository.save(new ReviewReaction(review, reaction));
            }


        }

    }
}
