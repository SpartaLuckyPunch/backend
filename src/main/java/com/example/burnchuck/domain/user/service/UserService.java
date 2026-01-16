package com.example.burnchuck.domain.user.service;

import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.auth.model.dto.AuthUser;
import com.example.burnchuck.domain.user.model.request.UserUpdateProfileRequest;
import com.example.burnchuck.domain.user.model.response.UserUpdateProfileResponse;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    /**
     * 내 정보 수정(닉네임, 주소)
     *
     * 고도화 작업 시, 프로필 이미지 수정 항목 추가 예정
     */
    @Transactional
    public UserUpdateProfileResponse updateProfile(AuthUser authUser, UserUpdateProfileRequest request) {

        // 1. 로그인한 유저 정보로 객체 생성
        User user = userRepository.findActivateUserById(authUser.getId());

        // 2. 닉네임 변경하는 경우, 새 닉네임의 중복 여부 확인
        String currentNickname = user.getNickname();
        String newNickname = request.getNickname();

        boolean isNicknameChanged = !Objects.equals(currentNickname, newNickname);
        boolean existNickname = userRepository.existsByNickname(newNickname);

        if (isNicknameChanged && existNickname) {
            throw new CustomException(ErrorCode.NICKNAME_EXIST);
        }

        // 3. 주소 조회 후 객체 생성
        Address newAddress = addressRepository.findAddressByAddressInfo(
            request.getProvince(),
            request.getCity(),
            request.getDistrict()
        );

        // 4. 정보 업데이트
        user.updateProfile(newNickname, newAddress);
        userRepository.saveAndFlush(user);

        return UserUpdateProfileResponse.from(user, newAddress);
    }
}
