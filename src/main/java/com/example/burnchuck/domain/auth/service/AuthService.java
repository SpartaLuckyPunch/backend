package com.example.burnchuck.domain.auth.service;

import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.JwtUtil;
import com.example.burnchuck.domain.auth.dto.request.*;
import com.example.burnchuck.domain.auth.dto.response.*;
import com.example.burnchuck.common.enums.Gender;
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

        String email = request.getEmail();
        String nickname = request.getNickname();

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_EXIST);
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_EXIST);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Gender gender = Gender.findEnum(request.getGender());

        Address address = addressRepository.findAddressByAddressInfo(request.getProvince(), request.getCity(), request.getDistrict());

        User user = new User(
            email, encodedPassword, nickname,
            request.getBirthDate(),
            gender.isValue(),
            address
        );

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), email, nickname);

        return new AuthSignupResponse(token);
    }

    /**
     * 로그인
     */
    @Transactional
    public AuthLoginResponse login(AuthLoginRequest request) {

        User user = userRepository.findActivateUserByEmail(request.getEmail());

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!matches) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getNickname());

        return new AuthLoginResponse(token);
    }
}
