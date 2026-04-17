package com.evently.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.context.SecurityContextHolder;

import com.evently.user_service.service.UserService;
import com.evently.user_service.dto.UserRequest;
import com.evently.user_service.entity.User;
import jakarta.validation.Valid;
import com.evently.user_service.dto.LoginRequest;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@Valid @RequestBody UserRequest request) {
        System.out.println("Register endpoint called");
        return userService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        System.out.println("Login endpoint called");
        return userService.login(request);
    }

    @GetMapping("/me")
    public String me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("/me endpoint called by: " + auth.getName());
        return "Email: " + auth.getName() + " | Role: " + auth.getAuthorities();
    }
}