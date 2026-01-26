package com.example.burnchuck.common.bootstrap.dataInitializer;

import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.Reaction;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.reaction.repository.ReactionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryReactionInitData implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final ReactionRepository reactionRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (categoryRepository.count() > 0 || reactionRepository.count() > 0 ) {
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

        List<Reaction> reactionList = List.of(
            new Reaction("친철하고 매너가 좋아요."),
            new Reaction("시간 약속을 잘 지켜요."),
            new Reaction("분위기를 잘 살려요."),
            new Reaction("유머 감각이 있어요."),
            new Reaction("이야기를 잘 들어줘요."),
            new Reaction("에너지가 좋아요."),
            new Reaction("적극적으로 참여해요."),
            new Reaction("아이디어를 잘 내요."),
            new Reaction("배려심이 있어요.")
        );

        reactionRepository.saveAll(reactionList);
    }
}
