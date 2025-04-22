package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.UserRegisterRequest;
import com.suryadeep.openshop.entity.User;
import jakarta.transaction.Transactional;

public interface AuthenticationService {

    @Transactional
    User registerUser(UserRegisterRequest registerRequest);

}
