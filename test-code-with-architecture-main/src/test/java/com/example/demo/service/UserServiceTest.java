package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceTest {

    @Autowired
    private UserService userService;

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


}