package com.example.burnchuck.domain.auth.service;

import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserRefresh;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.enums.Gender;
import com.example.burnchuck.common.enums.Provider;
import com.example.burnchuck.common.enums.UserRole;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.JwtUtil;
import com.example.burnchuck.domain.auth.dto.request.AuthLoginRequest;
import com.example.burnchuck.domain.auth.dto.request.AuthReissueTokenRequest;
import com.example.burnchuck.domain.auth.dto.request.AuthSignupRequest;
import com.example.burnchuck.domain.auth.dto.response.AuthTokenResponse;
import com.example.burnchuck.domain.auth.dto.response.KakaoUserInfoResponse;
import com.example.burnchuck.domain.auth.repository.UserRefreshRepository;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserRefreshRepository userRefreshRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KakaoService kakaoService;
    /**
     * 회원가입
     */
    public AuthTokenResponse signup(AuthSignupRequest request) {

        User user = createUser(request);

        return generateToken(user);
    }

    /**
     * 로그인
     */
    @Transactional
    public AuthTokenResponse login(AuthLoginRequest request) {

        User user = userRepository.findActivateUserByEmail(request.getEmail());

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!matches) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        return generateToken(user);
    }

    @Transactional
    public User createUser(AuthSignupRequest request) {

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
                address,
                UserRole.USER,
                Provider.LOCAL,null
        );

        userRepository.save(user);

        return user;
    }

    @Transactional
    public AuthTokenResponse generateToken(User user) {

        Long userId = user.getId();

        String accessToken = jwtUtil.generateAccessToken(userId, user.getEmail(), user.getNickname(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        boolean exist = userRefreshRepository.existsByUserId(userId);

        UserRefresh userRefresh;

        if (exist) {
            userRefresh = userRefreshRepository.findUserRefreshByUserId(userId);
            userRefresh.updateRefreshToken(refreshToken);
        } else {
            userRefresh = new UserRefresh(user, refreshToken);
        }

        userRefreshRepository.save(userRefresh);

        return new AuthTokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthTokenResponse reissueToken(AuthReissueTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtUtil.extractId(refreshToken);

        UserRefresh userRefresh = userRefreshRepository.findUserRefreshByUserId(userId);

        if (!ObjectUtils.nullSafeEquals(refreshToken, userRefresh.getRefreshToken())) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRefresh.getUser();

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getNickname(), user.getRole());

        if (jwtUtil.expireInTwoDays(refreshToken)) {
            refreshToken = jwtUtil.generateRefreshToken(userId);
            userRefresh.updateRefreshToken(refreshToken);
        }

        return new AuthTokenResponse(accessToken, refreshToken);
    }

    /**
     * 소셜로그인/회원가입 통합 처리 
     */
    @Transactional
    public AuthTokenResponse socialLogin(String code, Provider provider) {

        String accessToken = kakaoService.getKakaoAccessToken(code);

        KakaoUserInfoResponse userInfo = kakaoService.getKakaoUserInfo(accessToken);

        String email = userInfo.getEmail();
        String nickname = userInfo.getNickname();
        String providerId = String.valueOf(userInfo.getId());

        Optional<User> optionalUser = userRepository.findByProviderAndProviderIdAndIsDeletedFalse(provider, providerId);

        User user;
        if (optionalUser.isEmpty()) {

            String tempPassword = passwordEncoder.encode(UUID.randomUUID().toString());

            Address defaultAddress = addressRepository.findById(1L)
                    .orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));

            // 유저 존재 X -> 신규 소셜 유저 생성
            user = new User(
                    email,
                    tempPassword,
                    nickname,
                    null,
                    false,
                    defaultAddress,
                    UserRole.USER,
                    provider,
                    providerId
            );
            userRepository.save(user);
        } else {
            // 유저 존재 O -> 기존 유저 정보 가져오기
            user = optionalUser.get();
        }

        return generateToken(user);
    }
}