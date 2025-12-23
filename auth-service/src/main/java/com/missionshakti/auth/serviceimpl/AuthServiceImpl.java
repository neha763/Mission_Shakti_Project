package com.missionshakti.auth.serviceimpl;


import com.missionshakti.auth.dto.CreateAuthUserRequest;
import com.missionshakti.auth.dto.LoginRequest;
import com.missionshakti.auth.dto.LoginResponse;
import com.missionshakti.auth.entity.AuthUser;
import com.missionshakti.auth.enums.Role;
import com.missionshakti.auth.repository.AuthUserRepository;
import com.missionshakti.auth.service.AuthService;
import com.missionshakti.auth.service.EmailService;
import com.missionshakti.auth.util.JwtUtil;
import com.missionshakti.auth.util.OtpUtil;
import com.missionshakti.auth.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthUserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {

        AuthUser user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));


        if (user.isTemporaryPassword() &&
                user.getPasswordExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Temporary password expired. Reset password.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return LoginResponse.builder()
                .username(user.getUsername())
                .role(user.getRole().name())
                .token(token)
                .build();
    }


    @Override
    public void forgotPassword(String email) {

        AuthUser user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered"));

        String otp = OtpUtil.generateOtp();

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(15));

        repository.save(user);

        emailService.sendOtp(email, otp);
    }


    @Override
    public void resetPassword(String email, String otp, String newPassword) {

        AuthUser user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid request"));

        if (user.getOtp() == null ||
                !user.getOtp().equals(otp) ||
                user.getOtpExpiry().isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Invalid or expired OTP");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);

        repository.save(user);
    }

    @Override
    public void createUserWithTempPassword(CreateAuthUserRequest req) {

        String tempPassword = PasswordUtil.generateTempPassword();

        AuthUser user = AuthUser.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .role(Role.valueOf(req.getRole()))
                .password(passwordEncoder.encode(tempPassword))
                .temporaryPassword(true)
                .passwordExpiry(LocalDateTime.now().plusHours(24))
                .build();

        repository.save(user);

        emailService.sendTempPasswordMail(
                req.getEmail(),
                req.getUsername(),
                tempPassword
        );
    }


}
