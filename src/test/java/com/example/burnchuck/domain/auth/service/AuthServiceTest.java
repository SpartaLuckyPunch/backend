package com.example.burnchuck.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.burnchuck.common.entity.Address;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserRefresh;
import com.example.burnchuck.common.enums.Gender;
import com.example.burnchuck.common.enums.Provider;
import com.example.burnchuck.common.enums.UserRole;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.JwtUtil;
import com.example.burnchuck.domain.auth.dto.request.AuthLoginRequest;
import com.example.burnchuck.domain.auth.dto.request.AuthSignupRequest;
import com.example.burnchuck.domain.auth.dto.response.AuthTokenResponse;
import com.example.burnchuck.domain.auth.repository.UserRefreshRepository;
import com.example.burnchuck.domain.user.repository.AddressRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import com.example.burnchuck.fixture.UserFixture;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserRefreshRepository userRefreshRepository;
    @Mock
    private KakaoService kakaoService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    Address address = UserFixture.address;

    @Test
    @DisplayName("회원가입 정상 처리")
    void signup_success() {

        // Given
        String email = "test@test.com";
        String nickname = "testUser";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        AuthSignupRequest request = new AuthSignupRequest(
            email,
            rawPassword,
            nickname,
            LocalDate.now(),
            "서울특별시",
            "강동구",
            "천호동",
            "여"
        );

        User user = new User(
            email,
            encodedPassword,
            nickname,
            LocalDate.now(),
            Gender.findEnum("여"),
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(addressRepository.findAddressByAddressInfo(anyString(), anyString(), anyString())).thenReturn(address);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn(refreshToken);
        when(userRefreshRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // When
        authService.signup(request);

        // Then
        verify(userRepository).saveAndFlush(any(User.class));
        verify(userRefreshRepository).save(any(UserRefresh.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_failure_duplicateEmail() {

        // Given
        String email = "test@test.com";
        String nickname = "testUser";
        String rawPassword = "rawPassword";

        AuthSignupRequest request = new AuthSignupRequest(
            email,
            rawPassword,
            nickname,
            LocalDate.now(),
            "서울특별시",
            "강동구",
            "천호동",
            "여"
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signup(request));

        assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_failure_duplicateNickname() {

        // Given
        String email = "test@test.com";
        String nickname = "testUser";
        String rawPassword = "rawPassword";

        AuthSignupRequest request = new AuthSignupRequest(
            email,
            rawPassword,
            nickname,
            LocalDate.now(),
            "서울특별시",
            "강동구",
            "천호동",
            "여"
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNickname(anyString())).thenReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signup(request));

        assertEquals("이미 사용 중인 닉네임입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 정상 처리")
    void login_success() {

        // Given
        String email = "test@test.com";
        String nickname = "testUser";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        AuthLoginRequest request = new AuthLoginRequest(
            email,
            rawPassword
        );

        User user = new User(
            email,
            encodedPassword,
            nickname,
            LocalDate.now(),
            Gender.findEnum("여"),
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findActivateUserByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn(refreshToken);
        when(userRefreshRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // When
        AuthTokenResponse response = authService.login(request);

        // Then
        assertThat(response.getToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("로그인 정상 처리 - 리프레시 토큰 재발급")
    void login_success_newRefreshToken() {

        // Given
        String email = "test@test.com";
        String nickname = "testUser";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        AuthLoginRequest request = new AuthLoginRequest(
            email,
            rawPassword
        );

        User user = new User(
            email,
            encodedPassword,
            nickname,
            LocalDate.now(),
            Gender.findEnum("여"),
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        UserRefresh userRefresh = new UserRefresh(user, refreshToken);
        ReflectionTestUtils.setField(userRefresh, "id", 1L);

        when(userRepository.findActivateUserByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn(refreshToken);
        when(userRefreshRepository.findByUserId(anyLong())).thenReturn(Optional.of(userRefresh));

        // When
        authService.login(request);

        // Then
        assertThat(userRefresh.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_failure_wrongPassword() {

        // Given
        String email = "test@test.com";
        String nickname = "testUser";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";

        AuthLoginRequest request = new AuthLoginRequest(
            email,
            rawPassword
        );

        User user = new User(
            email,
            encodedPassword,
            nickname,
            LocalDate.now(),
            Gender.findEnum("여"),
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findActivateUserByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.login(request));

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("access 토큰 재발급")
    void reissueToken_success() {

        // Given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        User user = new User(
            "test@test.com",
            "encodedPassword",
            "testUser",
            LocalDate.now(),
            Gender.findEnum("여"),
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        UserRefresh userRefresh = new UserRefresh(user, refreshToken);
        ReflectionTestUtils.setField(userRefresh, "id", 1L);

        when(jwtUtil.isExpired(anyString())).thenReturn(false);
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractId(anyString())).thenReturn(1L);
        when(userRefreshRepository.findUserRefreshByUserId(anyLong())).thenReturn(userRefresh);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.expireInTwoDays(anyString())).thenReturn(false);

        // When
        AuthTokenResponse response = authService.reissueToken(refreshToken);

        // Then
        assertThat(response.getToken()).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("access 토큰 재발급, refresh 토큰 재발급")
    void reissueToken_success_newRefreshToken() {

        // Given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String newRefreshToken = "newRefreshToken";

        User user = new User(
            "test@test.com",
            "encodedPassword",
            "testUser",
            LocalDate.now(),
            Gender.findEnum("여"),
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        UserRefresh userRefresh = new UserRefresh(user, refreshToken);
        ReflectionTestUtils.setField(userRefresh, "id", 1L);

        when(jwtUtil.isExpired(anyString())).thenReturn(false);
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractId(anyString())).thenReturn(1L);
        when(userRefreshRepository.findUserRefreshByUserId(anyLong())).thenReturn(userRefresh);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.expireInTwoDays(anyString())).thenReturn(true);
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn(newRefreshToken);

        // When
        AuthTokenResponse response = authService.reissueToken(refreshToken);

        // Then
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("재발급 실패 - 만료된 토큰")
    void reissueToken_failure_expired() {

        // Given
        String refreshToken = "refreshToken";

        when(jwtUtil.isExpired(anyString())).thenReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.reissueToken(refreshToken));

        assertEquals("유효 기간이 만료된 토큰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("재발급 실패 - DB 저장 내용과 불일치")
    void reissueToken_failure_invalid() {

        // Given
        String refreshToken = "refreshToken";
        String differentRefreshToken = "differentRefreshToken";

        User user = new User(
            "test@test.com",
            "encodedPassword",
            "testUser",
            LocalDate.now(),
            Gender.findEnum("여"),
            address,
            UserRole.USER,
            Provider.LOCAL,
            null
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        UserRefresh userRefresh = new UserRefresh(user, differentRefreshToken);
        ReflectionTestUtils.setField(userRefresh, "id", 1L);

        when(jwtUtil.isExpired(anyString())).thenReturn(false);
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractId(anyString())).thenReturn(1L);
        when(userRefreshRepository.findUserRefreshByUserId(anyLong())).thenReturn(userRefresh);

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.reissueToken(refreshToken));

        assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
    }
}