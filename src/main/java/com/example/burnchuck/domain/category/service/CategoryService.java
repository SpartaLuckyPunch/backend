package com.example.burnchuck.domain.category.service;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.domain.category.model.response.CategoryResponse;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategory() {

        List<Category> categories = categoryRepository.findAll();

        List<CategoryResponse> dtos = new ArrayList<>();

        for (CategoryResponse dto : dtos) {
            dtos.add(categories.)
        }

        return dtos;
    }
}
