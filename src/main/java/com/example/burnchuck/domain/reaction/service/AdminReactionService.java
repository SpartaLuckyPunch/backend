//package com.example.burnchuck.domain.reaction.service;
//
//import com.example.burnchuck.common.entity.Reaction;
//import com.example.burnchuck.domain.reaction.dto.request.ReactionCreateRequest;
//import com.example.burnchuck.domain.reaction.dto.response.ReactionCreateResponse;
//import com.example.burnchuck.domain.reaction.repository.ReactionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//
//@Service
//@RequiredArgsConstructor
//public class AdminReactionService {
//
//    private final ReactionRepository reactionRepository;
//
//    /**
//     * 리액션 종류 생성
//     */
//    public ReactionCreateResponse createAdminReaction(ReactionCreateRequest request) {
//
//        Reaction reaction = new Reaction(request.getReaction());
//
//        return ReactionCreateResponse.from(reactionRepository.save(reaction));
//    }
//
//    /**
//     * 리액션 종류 삭제
//     */
//    public void deleteAdminReaction(Long reactionId) {
//
//        Reaction reaction = reactionRepository.findReactionById(reactionId);
//
//        reactionRepository.delete(reaction);
//    }
//
//}
