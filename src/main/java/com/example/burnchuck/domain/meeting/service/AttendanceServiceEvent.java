package com.example.burnchuck.domain.meeting.service;


import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceServiceEvent {

    private final Meeting meeting;
    private final User user;
}
