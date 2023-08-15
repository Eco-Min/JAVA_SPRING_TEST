package com.example.demo.user.domain;

import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private final static String UUID_VALUE = "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaaaa";
    private final static String UUID_VALUE2 = "bbbbbbbbbbbb-bbbb-bbbbbb-bbbb-bbbbbbbbbbbbbb";
    private final static long CLOCK_VALUE = 1678530673598L;

    @Test
    public void create_User_from_UserCreate_dto() throws Exception {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("eco@naver.com")
                .nickname("eco")
                .address("Seoul")
                .build();

        // when
        User result = User.from(userCreate, new TestUuidHolder(UUID_VALUE));

        // then
        assertThat(result.getId()).isNull();
        assertThat(result.getEmail()).isEqualTo(userCreate.getEmail());
        assertThat(result.getNickname()).isEqualTo(userCreate.getNickname());
        assertThat(result.getAddress()).isEqualTo(userCreate.getAddress());
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo(UUID_VALUE);
    }

    @Test
    public void update_user_form_UserUpdate_dto() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("eco@naver.com")
                .nickname("eco")
                .address("seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode(UUID_VALUE)
                .build();

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("eco-update")
                .address("Busan")
                .build();

        // when
        User result = user.update(userUpdate);


        // then
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getNickname()).isEqualTo(userUpdate.getNickname());
        assertThat(result.getAddress()).isEqualTo(userUpdate.getAddress());
        assertThat(result.getStatus()).isEqualTo(user.getStatus());
        assertThat(result.getCertificationCode()).isEqualTo(user.getCertificationCode());
    }

    @Test
    public void user_can_login_when_login_change_lastLoginAt() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("eco@naver.com")
                .nickname("eco")
                .address("seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode(UUID_VALUE)
                .build();

        // when
        User result = user.login(new TestClockHolder(CLOCK_VALUE));


        // then
        assertThat(result.getLastLoginAt()).isEqualTo(CLOCK_VALUE);
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
        assertThat(result.getAddress()).isEqualTo(user.getAddress());
        assertThat(result.getStatus()).isEqualTo(user.getStatus());
        assertThat(result.getCertificationCode()).isEqualTo(user.getCertificationCode());
    }

    @Test
    public void user_can_activate_certificationCode_userAccount() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("eco@naver.com")
                .nickname("eco")
                .address("seoul")
                .status(UserStatus.PENDING)
                .certificationCode(UUID_VALUE)
                .build();

        // when
        User result = user.certificate(UUID_VALUE);


        // then
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
        assertThat(result.getAddress()).isEqualTo(user.getAddress());
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getCertificationCode()).isEqualTo(user.getCertificationCode());
    }

    @Test
    public void user_can_not_activate_wrong_certificationCode_userAccount() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("eco@naver.com")
                .nickname("eco")
                .address("seoul")
                .status(UserStatus.PENDING)
                .certificationCode(UUID_VALUE)
                .build();

        // when
        // then
        assertThatThrownBy(() -> user.certificate(UUID_VALUE2))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }

}