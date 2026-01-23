package com.example.burnchuck.domain.user.service;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Reaction;
import com.example.burnchuck.domain.category.dto.request.AdminCategoryCreateRequest;
import com.example.burnchuck.domain.reaction.dto.request.AdminReactionCreateRequest;
import com.example.burnchuck.domain.category.dto.response.AdminCategoryCreateResponse;
import com.example.burnchuck.domain.reaction.dto.response.AdminReactionCreateResponse;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.reaction.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CategoryRepository categoryRepository;
    private final ReactionRepository reactionRepository;

    /**
     * 카테고리 생성
     */
    @Transactional
    public AdminCategoryCreateResponse createAdminCategory(AdminCategoryCreateRequest request) {

        Category category = new Category(request.getCode(), request.getCode());

        return AdminCategoryCreateResponse.from(categoryRepository.save(category));


    }

    /**
     * 카테고리 삭제
     */
    public void deleteAdminCategory(Long categoryId) {

        Category category = categoryRepository.findCategoryById(categoryId);

        categoryRepository.delete(category);
    }

    /**
     * 리액션 종류 생성
     */
    public AdminReactionCreateResponse createAdminReaction(AdminReactionCreateRequest request) {

        Reaction reaction = new Reaction(request.getReaction());

        return AdminReactionCreateResponse.from(reactionRepository.save(reaction));
    }

    /**
     * 리액션 종류 삭제
     */
    public void deleteAdminReaction(Long reactionId) {

        Reaction reaction = reactionRepository.findReactionById(reactionId);

        reactionRepository.delete(reaction);
    }


}
