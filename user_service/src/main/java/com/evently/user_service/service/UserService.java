package com.evently.user_service.service;

import com.evently.user_service.entity.User;
import com.evently.user_service.repository.UserRepository;
import com.evently.user_service.security.JwtUtil;
import com.evently.user_service.dto.LoginRequest;
import com.evently.user_service.dto.UserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginRequest request) {
        System.out.println("🔐 Login attempt for: " + request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("✅ User found: " + user.getEmail() + ", Role: " + user.getRole());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("❌ Invalid password for: " + request.getEmail());
            throw new RuntimeException("Invalid password");
        }

        System.out.println("✅ Password matched for: " + request.getEmail());
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        System.out.println("🎫 Token generated successfully");
        
        return token;
    }

    public User register(UserRequest request) {
        System.out.println("📝 Registering user: " + request.getEmail());
        
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole().toUpperCase() : "USER");

        User savedUser = userRepository.save(user);
        System.out.println("User registered successfully with role: " + savedUser.getRole());
        
        return savedUser;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User updateUser(Long id, UserRequest request) {
        User user = getUserById(id);

        if (request.getName() != null)
            user.setName(request.getName());

        if (request.getUsername() != null)
            user.setUsername(request.getUsername());

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    public User updateUserRole(Long id, String role) {
        List<String> validRoles = List.of("ADMIN", "EMPLOYEE", "USER");

        if (!validRoles.contains(role.toUpperCase())) {
            throw new RuntimeException("Invalid role. Valid roles: ADMIN, EMPLOYEE, USER");
        }

        User user = getUserById(id);
        user.setRole(role.toUpperCase());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}