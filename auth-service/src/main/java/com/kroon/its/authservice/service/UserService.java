package com.kroon.its.authservice.service;

import com.kroon.its.authservice.dto.RegistrationRequest;
import com.kroon.its.authservice.dto.RegistrationResponse;
import com.kroon.its.authservice.exception.ResourceConflictException;
import com.kroon.its.authservice.repository.RoleRepository;
import com.kroon.its.authservice.repository.UserRepository;
import com.kroon.its.authservice.entity.Role;
import com.kroon.its.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {

        validate(request);

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceConflictException("User already exists");
        }
        Role role = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException("Default role is not configured"));

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .build();

        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        return RegistrationResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();

    }

    private static void validate(RegistrationRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Registration request must not be null"
            );
        }

        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException(
                    "Email must not be blank"
            );
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException(
                    "Password must not be blank"
            );
        }
    }
}
