package com.missionshakti.auth.serviceimpl;


import com.missionshakti.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Override
    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Mission Shakti - Password Reset OTP");
        message.setText(
                "Your OTP is: " + otp +
                        "\nThis OTP is valid for 15 minutes.\n\n" +
                        "Do not share this OTP with anyone."
        );

        mailSender.send(message);
    }

    @Override
    public void sendTempPasswordMail(String to, String username, String password) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Mission Shakti - Account Created");

        message.setText("""
        Hello %s,

        Your account has been created.

        Username: %s
        Temporary Password: %s

        This password is valid for 24 hours.
        Please reset your password after login.

        Regards,
        Mission Shakti Team
        """.formatted(username, username, password));

        mailSender.send(message);
    }

}
