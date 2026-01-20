package com.example.burnchuck.domain.attendance.repository;

import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.domain.meeting.model.dto.MeetingSummaryDto;
import java.util.List;

public interface UserMeetingCustomRepository {

    List<MeetingSummaryDto> findAllMeetingsByUser(User user);

    List<UserMeeting> findMeetingMembers(Long meetingId);
}
