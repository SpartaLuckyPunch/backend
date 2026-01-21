package com.example.burnchuck.domain.category.repository;

import static com.example.burnchuck.common.enums.ErrorCode.CATEGORY_NOT_FOUND;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByIdAsc();

    default Category findCategoryById(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));
    }
}
