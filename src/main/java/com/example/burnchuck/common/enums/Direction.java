package com.example.burnchuck.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Direction {

    NORTH(0.0),
    NORTHEAST(45.0),
    EAST(90.0),
    SOUTHEAST(135.0),
    SOUTH(180.0),
    SOUTHWEST(225.0),
    WEST(270.0),
    NORTHWEST(315.0);

    private final double bearing;
}
