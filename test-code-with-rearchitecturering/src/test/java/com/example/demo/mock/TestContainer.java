package com.example.demo.mock;

import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.user.controller.UserController;
import com.example.demo.user.controller.UserCreateController;
import com.example.demo.user.controller.port.*;
import com.example.demo.user.service.CertificationServiceImpl;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.MailSender;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;

public class TestContainer {
    public final MailSender mailSender;
    public final UserRepository userRepository;
    public final CertificationService certificationService;
    public final UserReadService userReadService;
    public final UserUpdateAndRemoveService userUpdateAndRemoveService;
    public final AuthenticationService authenticationService;
    public final UserCreateService userCreateService;
    public final UserController userController;
    public final UserCreateController userCreateController;

    @Builder
    private TestContainer(ClockHolder clockHolder, UuidHolder uuidHolder) {
        this.mailSender = new FakeMailSender();
        this.userRepository = new FakeUserRepository();
        this.certificationService = new CertificationServiceImpl(this.mailSender);
        UserServiceImpl userService = new UserServiceImpl(
                this.userRepository,
                this.certificationService,
                uuidHolder,
                clockHolder
        );

        this.userReadService = userService;
        this.userUpdateAndRemoveService = userService;
        this.authenticationService = userService;
        this.userCreateService = userService;
        this.userController = UserController.builder()
                .userReadService(userReadService)
                .userUpdateAndRemoveService(userUpdateAndRemoveService)
                .authenticationService(authenticationService)
                .build();

        this.userCreateController = new UserCreateController(userCreateService);

    }

}
