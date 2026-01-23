package com.example.burnchuck.domain.category.service;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.domain.category.dto.request.AdminCategoryCreateRequest;
import com.example.burnchuck.domain.category.dto.response.AdminCategoryCreateResponse;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

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
}
