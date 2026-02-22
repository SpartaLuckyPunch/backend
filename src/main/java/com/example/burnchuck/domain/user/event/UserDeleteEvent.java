package com.example.burnchuck.domain.user.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDeleteEvent {

    private Long userId;
}
