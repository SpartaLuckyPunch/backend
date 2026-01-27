package com.example.burnchuck.common.bootstrap.dataInitializer;

import com.example.burnchuck.common.entity.Reaction;
import com.example.burnchuck.domain.reaction.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReactionInitData implements ApplicationRunner {

    private final ReactionRepository reactionRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (reactionRepository.count() > 0) {
            return;
        }

        List<Reaction> reactionList = List.of(
            new Reaction("친절하고 매너가 좋아요."),
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
