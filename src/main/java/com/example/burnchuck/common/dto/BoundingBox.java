package com.example.burnchuck.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoundingBox {

    private final double minLat;
    private final double maxLat;
    private final double minLng;
    private final double maxLng;
}
