package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import com.example.demo.user.service.port.MailSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CertificationServiceTest {

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