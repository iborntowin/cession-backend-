package com.example.cessionappbackend.controllers;

import com.example.cessionappbackend.dto.AuthResponse;
import com.example.cessionappbackend.dto.LoginRequest;
import com.example.cessionappbackend.dto.SignupRequest;
import com.example.cessionappbackend.dto.UserDto;
import com.example.cessionappbackend.entities.User;
import com.example.cessionappbackend.repositories.UserRepository;
import com.example.cessionappbackend.services.AuthService;
import com.example.cessionappbackend.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600) // Consider removing if handled by SecurityConfig
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Received login request for email: {}", loginRequest.getEmail());
        
        try {
            // Check if user exists
            if (!userRepository.existsByEmail(loginRequest.getEmail())) {
                logger.error("User not found with email: {}", loginRequest.getEmail());
                return ResponseEntity.status(403)
                    .body("Invalid email or password");
            }

            // Attempt authentication
            AuthResponse authResponse = authService.login(loginRequest);
            logger.info("User authenticated successfully: {}", loginRequest.getEmail());

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Authentication failed for user: {} - Error: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(403)
                .body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        User user = authService.registerUser(signupRequest);

        // Optionally return AuthResponse after signup for immediate login
        // AuthResponse authResponse = authService.signup(signupRequest);
        // return ResponseEntity.ok(authResponse);

        // Returning a simple success message for now
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserDto> userDtos = users.stream()
                .map(user -> new UserDto(user.getEmail(), user.getFullName(), user.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(new UserDto(user.getEmail(), user.getFullName(), user.getRole()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<?> deleteUserByEmail(@PathVariable String email) {
        userService.deleteByEmail(email);
        return ResponseEntity.ok("User deleted successfully!");
    }
}
