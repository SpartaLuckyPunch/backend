package com.example.burnchuck.common.bootstrap.dataInitializer;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInitData implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (categoryRepository.count() > 0) {
            return;
        }

        List<Category> categoryList = List.of(
            new Category("sports", "운동"),
            new Category("food", "식사"),
            new Category("drink", "술"),
            new Category("entertainment", "게임/오락"),
            new Category("study", "스터디"),
            new Category("music", "음악"),
            new Category("culture", "문화/공연/축제"),
            new Category("etc", "기타")
        );

        categoryRepository.saveAll(categoryList);
    }
}
