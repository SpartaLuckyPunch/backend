package com.example.burnchuck.common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false)
    private LocalDate birth;

    // false = 남자, true = 여자
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean gender;

    @Column
    private String profileImgUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    public User(String email, String password, String nickname, LocalDate birth, boolean gender, Address address) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.address = address;
    }

    public void uploadProfileImg(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void updateProfile(String nickname, Address address) {
        this.nickname = nickname;
        this.address = address;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
