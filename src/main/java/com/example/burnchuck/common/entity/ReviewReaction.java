package com.example.burnchuck.common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_reactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewReaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reaction_id", nullable = false)
    private Reaction reaction;

    public ReviewReaction(Review review, Reaction reaction) {
        this.review = review;
        this.reaction = reaction;
    }
}
