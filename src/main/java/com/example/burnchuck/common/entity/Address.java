package com.example.burnchuck.common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses",
        indexes = {@Index(name = "idx_address_province_city_district", columnList = "province, city, district")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

    private String district;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    public Address(String province, String city, String district, double latitude, double longitude) {
        this.province = province;
        this.city = city;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
