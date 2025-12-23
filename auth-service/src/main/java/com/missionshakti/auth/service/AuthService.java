package com.missionshakti.auth.service;


import com.missionshakti.auth.dto.CreateAuthUserRequest;
import com.missionshakti.auth.dto.LoginRequest;
import com.missionshakti.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
    void forgotPassword(String email);

    void resetPassword(String email, String otp, String newPassword);

    void createUserWithTempPassword(CreateAuthUserRequest req);
}
