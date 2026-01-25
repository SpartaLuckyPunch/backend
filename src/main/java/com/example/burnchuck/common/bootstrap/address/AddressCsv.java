package com.example.burnchuck.common.bootstrap.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressCsv {

    private String province;
    private String city;
    private String district;
    private double latitude;
    private double longitude;
}