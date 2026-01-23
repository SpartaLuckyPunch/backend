package com.example.burnchuck.domain.reaction.service;

import com.example.burnchuck.common.entity.Reaction;
import com.example.burnchuck.domain.reaction.dto.request.ReactionCreateRequest;
import com.example.burnchuck.domain.reaction.dto.response.ReactionCreateResponse;
import com.example.burnchuck.domain.reaction.repository.ReactionRepository;
import com.example.burnchuck.domain.review.dto.response.ReactionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;

    /**
     * 전체 후기 리액션 조회
     */
    @Transactional(readOnly = true)
    public List<ReactionResponse> getReviewReactionList() {

        return reactionRepository.findAll()
            .stream()
            .map(ReactionResponse::from)
            .toList();
    }

    /**
     * 리액션 종류 생성(관리자 전용)
     */
    public ReactionCreateResponse createReaction(ReactionCreateRequest request) {

        Reaction reaction = new Reaction(request.getReaction());

        return ReactionCreateResponse.from(reactionRepository.save(reaction));
    }

    /**
     * 리액션 종류 삭제(관리자 전용)
     */
    public void deleteReaction(Long reactionId) {

        Reaction reaction = reactionRepository.findReactionById(reactionId);

        reactionRepository.delete(reaction);
    }
}
