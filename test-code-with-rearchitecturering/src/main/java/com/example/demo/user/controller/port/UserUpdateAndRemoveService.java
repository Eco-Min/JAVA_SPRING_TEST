package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserUpdate;

public interface UserUpdateAndRemoveService {
    User update(long id, UserUpdate userUpdate);

    void removeAll();
}
