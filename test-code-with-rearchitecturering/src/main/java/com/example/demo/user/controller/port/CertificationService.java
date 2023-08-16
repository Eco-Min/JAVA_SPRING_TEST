package com.example.demo.user.controller.port;

public interface CertificationService {
    void send(String address, long id, String certificationCode);
}
