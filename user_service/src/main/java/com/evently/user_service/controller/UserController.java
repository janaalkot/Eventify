package com.evently.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import com.evently.user_service.service.UserService;
import com.evently.user_service.dto.UserRequest;
import com.evently.user_service.entity.User;
import jakarta.validation.Valid;
import com.evently.user_service.dto.LoginRequest;
import com.evently.user_service.security.JwtUtil;

import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    private final JwtUtil jwtUtil;


    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;//Inject JwtUtil
    }

    @PostMapping("/register")
    public User register(@Valid @RequestBody UserRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/{email}")
    public User getUser(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    @GetMapping("/me")
    public String me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        return "Email: " + auth.getName() +
               " Role: " + auth.getAuthorities();
    }

    @GetMapping("/admin/test")
    public String admin() {
        return "ADMIN ACCESS OK";
    }

    @GetMapping("/events/manage/test")
    public String employee() {
        return "EMPLOYEE ACCESS OK";
    }

    @GetMapping("/events/test")
    public String user() {
        return "USER ACCESS OK";
    }
    @GetMapping("/test")
    public String test() {
        return "You accessed a protected endpoint";
    }
}
