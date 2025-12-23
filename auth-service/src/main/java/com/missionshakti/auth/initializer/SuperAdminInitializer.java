package com.missionshakti.auth.initializer;



import com.missionshakti.auth.entity.AuthUser;
import com.missionshakti.auth.enums.Role;
import com.missionshakti.auth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminInitializer.class);

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        final String username = "superadmin";

        if (authUserRepository.findByUsername(username).isPresent()) {
            log.info("Super Admin already exists. Skipping creation.");
            return;
        }

        AuthUser superAdmin = AuthUser.builder()
                .username(username)
                .password(passwordEncoder.encode("SuperAdmin@123"))
                .role(Role.SUPER_ADMIN)
                .active(true)
                .build();

        authUserRepository.save(superAdmin);

        log.info("Super Admin created successfully");
    }
}
