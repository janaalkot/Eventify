package com.evently.user_service.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

import com.evently.user_service.dto.UserRequest;
import com.evently.user_service.entity.User;
import com.evently.user_service.service.UserService;

@RestController
@RequestMapping("/users/admin")
public class AdminController {
    
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        System.out.println("Admin - Get all users called");
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        System.out.println("Admin - Update user called for id: " + id);
        return userService.updateUser(id, request);
    }

    @PutMapping("/role/{id}")
    public User updateUserRole(@PathVariable Long id, @RequestParam String role) {
        System.out.println("Admin - Update role called for id: " + id + " to role: " + role);
        return userService.updateUserRole(id, role);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        System.out.println("Admin - Delete user called for id: " + id);
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}