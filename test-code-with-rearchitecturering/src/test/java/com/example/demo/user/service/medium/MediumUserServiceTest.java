package com.example.demo.user.service.medium;

import com.example.demo.user.domain.User;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.UserServiceImpl;
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
class MediumUserServiceTest {

    @Autowired
    private UserServiceImpl userService;
    @MockBean
    private JavaMailSender mailSender;

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
        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        User result = userService.create(userCreate);
        System.out.println("result = " + result);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        // CertificationCode 에 값이 UUID라 지금당장 test 방법이 없다
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
        assertThat(result.getLastLoginAt()).isGreaterThan(0);
    }

    @Test
    public void pendingUser_to_active_authCode() throws Exception {
        // given
        // when
        userService.verifyEmail(2, "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaabb");

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
            userService.verifyEmail(2, "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaabc");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}