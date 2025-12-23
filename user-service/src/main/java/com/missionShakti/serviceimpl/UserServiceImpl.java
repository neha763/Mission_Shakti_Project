package com.missionShakti.serviceimpl;

import com.missionShakti.dto.CreateUserRequest;
import com.missionShakti.dto.UserResponse;
import com.missionShakti.entity.User;
import com.missionShakti.exception.UnauthorizedActionException;
import com.missionShakti.exception.UserAlreadyExistsException;
import com.missionShakti.repository.UserRepository;
import com.missionShakti.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final WebClient.Builder webClientBuilder;

    @Override
    public UserResponse createUser(CreateUserRequest req) {

        userRepository.findByUsername(req.getUsername())
                .ifPresent(u -> {
                    throw new UserAlreadyExistsException("Username already exists");
                });

        userRepository.findByEmail(req.getEmail())
                .ifPresent(u -> {
                    throw new UserAlreadyExistsException("Email already exists");
                });

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .role(req.getRole())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);


        webClientBuilder.build()
                .post()
                .uri("http://localhost:8081/api/auth/internal/create-user")
                .bodyValue(Map.of(
                        "username", saved.getUsername(),
                        "email", saved.getEmail(),
                        "role", saved.getRole().name()
                ))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        return UserResponse.builder()
                .userId(saved.getUserId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole())
                .active(saved.getActive())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public UserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
