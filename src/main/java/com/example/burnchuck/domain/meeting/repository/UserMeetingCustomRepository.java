package com.example.burnchuck.domain.meeting.repository;

import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserMeeting;
import com.example.burnchuck.domain.meeting.model.response.AttendanceMeetingResponse;
import java.util.List;

public interface UserMeetingCustomRepository {

    List<AttendanceMeetingResponse> findAllMeetingsByUser(User user);

    List<UserMeeting> findMeetingMembers(Long meetingId);
}
