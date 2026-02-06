package com.example.burnchuck.common.utils;

import com.example.burnchuck.common.entity.User;

public class UserDisplay {

    public static String resolveNickname(User user) {
        if (user == null || user.isDeleted()) {
            return "탈퇴한 유저";
        }
        return user.getNickname();
    }

    public static String resolveProfileImg(User user) {
        if (user == null || user.isDeleted()) {
            return null;
        }
        return user.getProfileImgUrl();
    }
}
