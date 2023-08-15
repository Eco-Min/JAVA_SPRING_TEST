package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MediumCertificationServiceTest {

    @Test
    public void check_to_send_email_and_content() throws Exception{
        // given
        FakeMailSender fakeMailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(fakeMailSender);

        // when
        certificationService.send("eco@naver.com", 1, "aaaaaaaaaaa-aaaa-aaaaa-aaaa-aaaaaaaaaaaa");

        // then
        assertThat(fakeMailSender.email).isEqualTo("eco@naver.com");
        assertThat(fakeMailSender.title).isEqualTo("Please certify your email address");
    }



}