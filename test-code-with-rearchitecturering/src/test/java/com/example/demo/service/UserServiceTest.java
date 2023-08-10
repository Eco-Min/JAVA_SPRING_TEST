package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void getByEmail_find_user_ActiveStatus() throws Exception {
        // given
        String email = "eco@naver.com";

        // when
        UserEntity result = userService.getByEmail(email);

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
            UserEntity result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void getById_find_user_ActiveStatus() throws Exception {
        // given
        // when
        UserEntity result = userService.getById(1L);

        // then
        assertThat(result.getNickname()).isEqualTo("eco");
    }

    @Test
    public void getById_none_find_user_notActiveStatus() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getById(2L);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void create_by_userCreateDto() throws Exception {
        // given
        userService.removeALl();
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("eco3@naver.com")
                .address("Busan")
                .nickname("eco3")
                .build();
        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        UserEntity result = userService.create(userCreateDto);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        // CertificationCode 에 값이 UUID라 지금당장 test 방법이 없다
    }

    @Test
    public void update_by_userUpdateDto() throws Exception {
        // given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .address("Incheon")
                .nickname("eco-update")
                .build();

        // when
        userService.update(1, userUpdateDto);

        // then
        UserEntity result = userService.getById(1L);
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
        UserEntity result = userService.getById(1L);
        // 시간 체크가 완벽하지 않아서 나중에 수정
        assertThat(result.getLastLoginAt()).isGreaterThan(0);
    }

    @Test
    public void pendingUser_to_active_authCode() throws Exception {
        // given
        // when
        userService.verifyEmail(2, "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaabb");

        // then
        UserEntity result = userService.getById(2);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void pendingUser_to_active_authCode_exception() throws Exception {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            userService.verifyEmail(2, "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaabc");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}