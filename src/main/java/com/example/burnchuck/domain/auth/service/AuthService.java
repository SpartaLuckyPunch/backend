package com.example.burnchuck.domain.auth.service;

import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.JwtUtil;
import com.example.burnchuck.domain.auth.model.request.AuthSignupRequest;
import com.example.burnchuck.domain.auth.model.response.AuthSignupResponse;
import com.example.burnchuck.domain.user.enums.Gender;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
    public AuthSignupResponse signup(AuthSignupRequest request) {

        // 1. 이메일, 닉네임 중복 여부 확인 (고도화 작업에서 API 분리 예정)
        String email = request.getEmail();
        String nickname = request.getNickname();

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_EXIST);
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_EXIST);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 성별 Enum 형태로 변경
        Gender gender = Gender.findEnum(request.getGender());

        // 4. 주소 조회
        Address address = addressRepository.findAddressByAddressInfo(request.getProvince(), request.getCity(), request.getDistrict());

        // 5. User 객체 생성 및 저장
        User user = new User(
            email, encodedPassword, nickname,
            request.getBirthDate(),
            gender.isValue(),
            address
        );

        userRepository.save(user);

        // 6. 토큰 생성
        String token = jwtUtil.generateToken(user.getId(), email, nickname);

        return new AuthSignupResponse(token);
    }
}
