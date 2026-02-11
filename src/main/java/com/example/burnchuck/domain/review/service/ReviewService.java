package com.example.burnchuck.domain.review.service;

import static com.example.burnchuck.common.enums.ErrorCode.SELF_REVIEW_NOT_ALLOWED;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.Reaction;
import com.example.burnchuck.common.entity.Review;
import com.example.burnchuck.common.entity.ReviewReaction;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.reaction.repository.ReactionRepository;
import com.example.burnchuck.domain.review.dto.request.ReviewCreateRequest;
import com.example.burnchuck.domain.review.dto.response.ReactionCount;
import com.example.burnchuck.domain.review.dto.response.ReactionResponse;
import com.example.burnchuck.domain.review.dto.response.ReviewDetailResponse;
import com.example.burnchuck.domain.review.dto.response.ReviewGetListResponse;
import com.example.burnchuck.domain.review.repository.ReviewReactionRepository;
import com.example.burnchuck.domain.review.repository.ReviewRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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

        User reviewer = userRepository.findActivateUserById(authUser.getId());

        User reviewee = userRepository.findActivateUserById(revieweeId);

        Meeting meeting = meetingRepository.findActivateMeetingById(request.getMeetingId());

        if (reviewRepository.existsByMeetingIdAndReviewerIdAndRevieweeId(meeting.getId(), reviewer.getId(), reviewee.getId())) {
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }

        if (ObjectUtils.nullSafeEquals(reviewer.getId(), reviewee.getId())) {
            throw new CustomException(SELF_REVIEW_NOT_ALLOWED);
        }

        Review review = new Review(
                request.getRating().intValue(),
                request.getDetailedReview(),
                reviewer,
                reviewee,
                meeting
        );
        reviewRepository.save(review);

        List<Long> requestReactionList = request.getReactionList();

        if (requestReactionList != null) {

            List<Reaction> reactionList = reactionRepository.findAll();

            List<ReviewReaction> chosenReactionList = reactionList.stream()
                .filter(reaction -> requestReactionList.contains(reaction.getId()))
                .map(reaction -> new ReviewReaction(review, reaction))
                .toList();

            reviewReactionRepository.saveAll(chosenReactionList);
        }
    }

    /**
     * 후기 목록조회
     */
    @Transactional(readOnly = true)
    public ReviewGetListResponse getReviewList(Long userId, Pageable pageable) {

        User user = userRepository.findActivateUserById(userId);

        List<ReactionCount> reactionCounts = reviewReactionRepository.countReactionsByRevieweeId(user.getId());

        Page<Review> reviewPage = reviewRepository.findAllByRevieweeId(user.getId(), pageable);

        return ReviewGetListResponse.of(reactionCounts, reviewPage);
    }

    /**
     * 후기 단건조회
     */
    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(Long reviewId) {

        Review review = reviewRepository.findReviewById(reviewId);

        List<ReactionResponse> reactionResponses = reviewReactionRepository.findAllByReviewId(reviewId)
                .stream()
                .map(rr -> new ReactionResponse(
                        rr.getReaction().getId(),
                        rr.getReaction().getReaction()
                ))
                .toList();

        return ReviewDetailResponse.of(review, reactionResponses);
    }
}
