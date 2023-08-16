package com.example.demo.user.service;

import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import com.example.demo.user.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private final static String UUID_VALUE = "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaaaa";
    private final static String UUID_VALUE2 = "bbbbbbbbbbbb-bbbb-bbbbbb-bbbb-bbbbbbbbbbbbbb";
    private final static long CLOCK_VALUE = 1678530673598L;
    private UserServiceImpl userService;

    @BeforeEach
    void init() {
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        FakeMailSender fakeMailSender = new FakeMailSender();
        ClockHolder clockHolder = new TestClockHolder(CLOCK_VALUE);
        UuidHolder uuidHolder = new TestUuidHolder(UUID_VALUE);

        fakeUserRepository.save(User.builder()
                .id(1L)
                .email("eco@naver.com")
                .nickname("eco")
                .address("Seoul")
                .certificationCode(UUID_VALUE)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());

        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("eco2@naver.com")
                .nickname("eco2")
                .address("Seoul")
                .certificationCode(UUID_VALUE2)
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build());

        this.userService = new UserServiceImpl(
                fakeUserRepository,
                new CertificationServiceImpl(fakeMailSender),
                uuidHolder,
                clockHolder);

    }

    @Test
    public void getByEmail_find_user_ActiveStatus() throws Exception {
        // given
        String email = "eco@naver.com";

        // when
        User result = userService.getByEmail(email);

        // then
        assertThat(result.getNickname()).isEqualTo("eco");
    }

    @Test
    public void getByEmail_none_find_user_notActiveStatus() throws Exception {
        // given
        String email = "eco2@naver.com";

        // when
        // then
        assertThatThrownBy(() -> {
            User result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void getById_find_user_ActiveStatus() throws Exception {
        // given
        // when
        User result = userService.getById(1L);

        // then
        assertThat(result.getNickname()).isEqualTo("eco");
    }

    @Test
    public void getById_none_find_user_notActiveStatus() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            User result = userService.getById(2L);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void create_by_userCreate() throws Exception {
        // given
        userService.removeAll();
        UserCreate userCreate = UserCreate.builder()
                .email("eco3@naver.com")
                .address("Busan")
                .nickname("eco3")
                .build();

        // when
        User result = userService.create(userCreate);
        System.out.println("result = " + result);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo(UUID_VALUE);
    }

    @Test
    public void update_by_userUpdate() throws Exception {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Incheon")
                .nickname("eco-update")
                .build();

        // when
        userService.update(1, userUpdate);

        // then
        User result = userService.getById(1L);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAddress()).isEqualTo("Incheon");
        assertThat(result.getNickname()).isEqualTo("eco-update");
    }

    @Test
    public void login_check_lastLoginAt() throws Exception {
        // given
        // when
        userService.login(1);

        // then
        User result = userService.getById(1L);
        // 시간 체크가 완벽하지 않아서 나중에 수정
        assertThat(result.getLastLoginAt()).isEqualTo(CLOCK_VALUE);
    }

    @Test
    public void pendingUser_to_active_authCode() throws Exception {
        // given
        // when
        userService.verifyEmail(2, UUID_VALUE2);

        // then
        User result = userService.getById(2);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void pendingUser_to_active_authCode_exception() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            userService.verifyEmail(2, UUID_VALUE);
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }

}