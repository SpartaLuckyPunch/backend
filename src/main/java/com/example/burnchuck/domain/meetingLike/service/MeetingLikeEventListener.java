package com.example.burnchuck.domain.meetingLike.service;

import com.example.burnchuck.domain.meetingLike.event.MeetingLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MeetingLikeEventListener {

    private final MeetingLikeCacheService meetingLikeCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void deleteHostedMeetings(MeetingLikeEvent event) {

        Long meetingId = event.getMeetingId();

        switch (event.getType()) {
            case INCREASE -> meetingLikeCacheService.increaseMeetingLike(meetingId);
            case DECREASE -> meetingLikeCacheService.decreaseMeetingLike(meetingId);
        }
    }
}
