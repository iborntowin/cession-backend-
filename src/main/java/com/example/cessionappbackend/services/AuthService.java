package com.example.cessionappbackend.services;

import com.example.cessionappbackend.dto.AuthResponse;
import com.example.cessionappbackend.dto.LoginRequest;
import com.example.cessionappbackend.dto.SignupRequest;
import com.example.cessionappbackend.entities.User;
import com.example.cessionappbackend.repositories.UserRepository;
import com.example.cessionappbackend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public Authentication authenticateUser(LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));
    }

    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFullNameFromParts(signupRequest.getFirstName(), signupRequest.getLastName());
        user.setRole("ADMIN");
        user.setActive(true);

        return userRepository.save(user);
    }

    public void updateLastLogin(User user) {
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFullNameFromParts(signupRequest.getFirstName(), signupRequest.getLastName());
        user.setRole("ADMIN");
        user.setActive(true);
        user.setLastLogin(LocalDateTime.now());

        // Save user
        User savedUser = userRepository.save(user);

        // Create authentication token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(),
                signupRequest.getPassword()
        );

        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);

        // Return response
        return new AuthResponse(token, savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticateUser(loginRequest);

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        // Update last login time
        updateLastLogin(user);

        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole());
        logger.info("Login successful for user: {}", user.getEmail());
        logger.info("AuthResponse details: email={}, fullName={}, role={}", authResponse.getEmail(), authResponse.getFullName(), authResponse.getRole());

        // Return response
        return authResponse;
    }
}
