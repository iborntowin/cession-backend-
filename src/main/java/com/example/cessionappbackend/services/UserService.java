package com.example.cessionappbackend.services;

import com.example.cessionappbackend.entities.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User findByEmail(String email);
    void deleteByEmail(String email);
} 