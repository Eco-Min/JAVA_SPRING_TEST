package com.example.demo.user.controller.medium;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class MediumHealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void healthCheck_httpStatus200() throws Exception{
        // given
        // when
        // then
        mockMvc.perform(get("/health_check.html")).andExpect(status().isOk());
    }

}