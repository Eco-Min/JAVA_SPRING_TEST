package com.example.demo.user.controller;

import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
class UserCreateControllerTest {

    private final static String UUID_VALUE = "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaaaa";
    private final static String UUID_VALUE2 = "bbbbbbbbbbbb-bbbb-bbbbbb-bbbb-bbbbbbbbbbbbbb";
    private final static long CLOCK_VALUE = 1678530673598L;
    @Test
    public void client_create_user() throws Exception{
        // given
        TestContainer testContainer = TestContainer.builder()
                .uuidHolder(() -> UUID_VALUE)
                .build();

        // when
        ResponseEntity<UserResponse> result = testContainer.userCreateController.createUser(
                UserCreate.builder()
                        .email("eco@naver.com")
                        .nickname("eco")
                        .address("Seoul")
                        .build()
        );

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("eco@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("eco");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getBody().getLastLoginAt()).isNull();
        assertThat(testContainer.userRepository.findById(1).get().getCertificationCode()).isEqualTo(UUID_VALUE);

    }
}