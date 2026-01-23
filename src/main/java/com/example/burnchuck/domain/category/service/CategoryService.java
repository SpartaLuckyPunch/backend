package com.example.burnchuck.domain.category.service;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.domain.category.dto.request.CategoryCreateRequest;
import com.example.burnchuck.domain.category.dto.response.CategoryCreateResponse;
import com.example.burnchuck.domain.category.dto.response.CategoryListResponse;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 목록 조회
     */
    public CategoryListResponse getCategoryList() {

        List<Category> categories = categoryRepository.findAllByOrderByIdAsc();

        return CategoryListResponse.from(categories);
    }
    /**
     * 카테고리 생성(관리자 전용)
     */
    @Transactional
    public CategoryCreateResponse createCategory(CategoryCreateRequest request) {

        Category category = new Category(request.getCode(), request.getCode());

        return CategoryCreateResponse.from(categoryRepository.save(category));


    }

    /**
     * 카테고리 삭제(관리자 전용)
     */
    public void deleteCategory(Long categoryId) {

        Category category = categoryRepository.findCategoryById(categoryId);

        categoryRepository.delete(category);
    }


}
