package com.example.burnchuck.domain.meeting.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.example.burnchuck.common.enums.ValidationMessage.*;

@Getter
@AllArgsConstructor
public class MeetingCreateRequest {

    @NotBlank(message = MEETING_TITLE_NOT_BLANK)
    @Size(max = 50, message = MEETING_TITLE_SIZE)
    private String title;

    @NotBlank(message = MEETING_DESCRIPTION_NOT_BLANK)
    @Size(max = 500, message = MEETING_DESCRIPTION_SIZE)
    private String description;

    @NotBlank(message = MEETING_IMG_URL_NOT_BLANK)
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = MEETING_IMG_URL_FORMAT
    )
    private String imgUrl;

    @NotBlank(message = MEETING_LOCATION_NOT_BLANK)
    private String location;

    @NotNull(message = MEETING_LATITUDE_NOT_NULL)
    @DecimalMin(value = "-90.0", message = MEETING_LATITUDE_RANGE)
    @DecimalMax(value = "90.0", message = MEETING_LATITUDE_RANGE)
    private Double latitude;

    @NotNull(message = MEETING_LONGITUDE_NOT_NULL)
    @DecimalMin(value = "-180.0", message = MEETING_LONGITUDE_RANGE)
    @DecimalMax(value = "180.0", message = MEETING_LONGITUDE_RANGE)
    private Double longitude;

    @NotNull(message = MEETING_MAX_ATTENDEES_NOT_NULL)
    private int maxAttendees;

    private long views;

    @NotNull(message = MEETING_DATETIME_NOT_NULL)
    @Future(message = MEETING_DATETIME_FUTURE)
    private LocalDateTime meetingDateTime;

    @NotNull(message = MEETING_CATEGORY_NOT_NULL)
    private Long categoryId;
}
