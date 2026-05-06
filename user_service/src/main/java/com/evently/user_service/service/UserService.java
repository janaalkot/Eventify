package com.evently.user_service.service;

import com.evently.common.security.AuthenticatedUser;
import com.evently.common.security.JwtService;
import com.evently.user_service.dto.LoginRequest;
import com.evently.user_service.dto.LoginResponse;
import com.evently.user_service.dto.UserProfileResponse;
import com.evently.user_service.dto.UserRequest;
import com.evently.user_service.entity.Role;
import com.evently.user_service.entity.User;
import com.evently.user_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${auth.bootstrap.admin.email:}")
    private String bootstrapAdminEmail;

    @Value("${auth.bootstrap.admin.password:}")
    private String bootstrapAdminPassword;

    @Value("${auth.bootstrap.admin.full-name:Eventify Admin}")
    private String bootstrapAdminFullName;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostConstruct
    public void initializeAdminIfConfigured() {
        if (bootstrapAdminEmail == null || bootstrapAdminEmail.isBlank()
                || bootstrapAdminPassword == null || bootstrapAdminPassword.isBlank()) {
            return;
        }

        if (userRepository.findByEmail(bootstrapAdminEmail.toLowerCase()).isPresent()) {
            return;
        }

        User admin = new User();
        admin.setFullName(bootstrapAdminFullName);
        admin.setEmail(bootstrapAdminEmail.toLowerCase());
        admin.setPassword(passwordEncoder.encode(bootstrapAdminPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    public UserProfileResponse register(UserRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        return toProfile(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
        );

        String token = jwtService.generateToken(authenticatedUser);
        return new LoginResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(AuthenticatedUser authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.id())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toProfile(user);
    }

    private UserProfileResponse toProfile(User user) {
        return new UserProfileResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }
}
