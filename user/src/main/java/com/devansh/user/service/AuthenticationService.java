package com.devansh.user.service;

import com.devansh.user.dto.AuthRequest;
import com.devansh.user.dto.AuthResponse;
import com.devansh.user.dto.UserRequest;
import com.devansh.user.dto.UserResponse;
import com.devansh.user.model.Role;
import com.devansh.user.model.User;
import com.devansh.user.repository.UserRepository;
import com.devansh.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Transactional
    public AuthResponse register(UserRequest request) {
        // Create and save user
        UserResponse userResponse = userService.createUser(request);
        User user = userService.findUserById(userResponse.getId());

        // Generate JWT token
        String jwtToken = generateToken(user);
        
        return buildAuthResponse(user, jwtToken);
    }

    public AuthResponse authenticate(AuthRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // Get user and generate token
        User user = userService.findUserByEmail(request.getEmail());
        String jwtToken = generateToken(user);
        
        return buildAuthResponse(user, jwtToken);
    }

    private String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        
        return jwtService.generateToken(extraClaims, user);
    }

    private AuthResponse buildAuthResponse(User user, String jwtToken) {
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .expiresIn(jwtService.getJwtExpiration())
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
