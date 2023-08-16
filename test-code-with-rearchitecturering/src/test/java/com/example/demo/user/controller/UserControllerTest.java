package com.example.demo.user.controller;

import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.user.infrastructure.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private final static String UUID_VALUE = "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaaaa";
    private final static String UUID_VALUE2 = "bbbbbbbbbbbb-bbbb-bbbbbb-bbbb-bbbbbbbbbbbbbb";
    private final static long CLOCK_VALUE = 1678530673598L;

    @Test
    public void client_get_userInfo_without_address() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(
                User.builder()
                        .email("eco@naver.com")
                        .nickname("eco")
                        .address("Seoul")
                        .certificationCode(UUID_VALUE)
                        .status(UserStatus.ACTIVE)
                        .lastLoginAt(100L)
                        .build());

        // when
        ResponseEntity<UserResponse> result = testContainer.userController.getUserById(1L);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("eco@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("eco");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100L);
    }

    @Test
    public void client_not_get_userInfo_withoutUserInfo_response404() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.userController.getUserById(1L);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void client_activate_userStatus_from_authCode() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(
                User.builder()
                        .email("eco@naver.com")
                        .nickname("eco")
                        .address("Seoul")
                        .certificationCode(UUID_VALUE)
                        .status(UserStatus.PENDING)
                        .lastLoginAt(100L)
                        .build());

        // when
        ResponseEntity<Void> result
                = testContainer.userController.verifyEmail(1L, UUID_VALUE);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302));
        assertThat(testContainer.userReadService.getById(1L).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void show_errorCode_if_not_matched_certificationCode() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(
                User.builder()
                        .email("eco@naver.com")
                        .nickname("eco")
                        .address("Seoul")
                        .certificationCode(UUID_VALUE)
                        .status(UserStatus.PENDING)
                        .lastLoginAt(100L)
                        .build());

        // when
        // then
        assertThatThrownBy(() -> testContainer.userController.verifyEmail(1L, UUID_VALUE2))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }

    @Test
    public void client_get_address_when_call_userInfo() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> CLOCK_VALUE)
                .build();

        testContainer.userRepository.save(
                User.builder()
                        .email("eco@naver.com")
                        .nickname("eco")
                        .address("Seoul")
                        .certificationCode(UUID_VALUE)
                        .status(UserStatus.ACTIVE)
                        .lastLoginAt(100L)
                        .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.getMyInfo("eco@naver.com");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("eco@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("eco");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getBody().getAddress()).isEqualTo("Seoul");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(CLOCK_VALUE);
    }

    @Test
    public void client_modify_userInfo() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        testContainer.userRepository.save(
                User.builder()
                        .email("eco@naver.com")
                        .nickname("eco")
                        .address("Seoul")
                        .certificationCode(UUID_VALUE)
                        .status(UserStatus.ACTIVE)
                        .lastLoginAt(100L)
                        .build());

        // when
        ResponseEntity<MyProfileResponse> result = testContainer.userController.updateMyInfo("eco@naver.com",
                UserUpdate.builder()
                        .address("Busan")
                        .nickname("eco-update")
                        .build());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("eco@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("eco-update");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getBody().getAddress()).isEqualTo("Busan");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100L);
    }
}