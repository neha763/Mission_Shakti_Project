package com.missionShakti.service;

import com.missionShakti.dto.CreateUserRequest;
import com.missionShakti.dto.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long userId);
}
