package com.missionshakti.auth.service;

public interface EmailService {
    void sendOtp(String toEmail, String otp);
    void sendTempPasswordMail(String email, String username, String password);

}
