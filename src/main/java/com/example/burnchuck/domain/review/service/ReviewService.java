package com.example.burnchuck.domain.review.service;

import static com.example.burnchuck.common.enums.ErrorCode.SELF_REVIEW_NOT_ALLOWED;

import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.Reaction;
import com.example.burnchuck.common.entity.Review;
import com.example.burnchuck.common.entity.ReviewReaction;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.reaction.repository.ReactionRepository;
import com.example.burnchuck.domain.review.model.request.ReviewCreateRequest;
import com.example.burnchuck.domain.review.model.response.ReactionCount;
import com.example.burnchuck.domain.review.model.response.ReactionResponse;
import com.example.burnchuck.domain.review.model.response.ReviewDetailResponse;
import com.example.burnchuck.domain.review.model.response.ReviewGetListResponse;
import com.example.burnchuck.domain.review.repository.ReviewReactionRepository;
import com.example.burnchuck.domain.review.repository.ReviewRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createReview(AuthUser authUser, Long revieweeId, ReviewCreateRequest request) {

        // 1. reviewer(리뷰 작성자)가 존재하는지 검증
        User reviewer = userRepository.findActivateUserById(authUser.getId());

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

    /**
     * 후기 목록조회
     */
    @Transactional(readOnly = true)
    public ReviewGetListResponse getReviewList(Long userId, Pageable pageable) {

        // 1. 유저 존재하는지 검증
        User user = userRepository.findActivateUserById(userId);

        // 2. 리액션 통계 조회
        List<ReactionCount> reactionCounts = reviewReactionRepository.countReactionsByRevieweeId(userId);

        // 3. 리뷰 목록 조회 (페이징 + 생성일시 내림차순)
        Page<Review> reviewPage = reviewRepository.findAllByRevieweeId(userId, pageable);

        // 4. Dto 반환
        return ReviewGetListResponse.of(reactionCounts, reviewPage);
    }

    /**
     * 후기 단건조회
     */
    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(Long reviewId) {

        // 1. 리뷰 엔티티 조회
        Review review = reviewRepository.findReviewById(reviewId);

        // 2. 리액션 리스트 조회
        List<ReactionResponse> reactionResponses = reviewReactionRepository.findAllByReviewId(reviewId)
                .stream()
                .map(rr -> new ReactionResponse(
                        rr.getReaction().getId(),
                        rr.getReaction().getReaction()
                ))
                .toList();

        // 3. 반환
        return ReviewDetailResponse.of(review, reactionResponses);
    }
}
