package com.campuscare.campuscare.service;

import com.campuscare.campuscare.dto.AuthResponse;
import com.campuscare.campuscare.dto.LoginRequest;
import com.campuscare.campuscare.dto.RegisterRequest;
import com.campuscare.campuscare.model.Role;
import com.campuscare.campuscare.model.User;
import com.campuscare.campuscare.repository.RoleRepository;
import com.campuscare.campuscare.repository.UserRepository;
import com.campuscare.campuscare.security.JwtUtil;
import com.campuscare.campuscare.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (request.admissionNo() != null && userRepository.findByAdmissionNo(request.admissionNo()).isPresent()) {
            throw new RuntimeException("Admission number already registered");
        }

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("STUDENT role not found"));

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setAdmissionNo(request.admissionNo());
        user.setPhone(request.phone());
        user.setRole(studentRole);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(savedUser, studentRole);
        String accessToken = jwtUtil.generateToken(userPrincipal);
        String refreshToken = jwtUtil.generateRefreshToken(userPrincipal);

        return new AuthResponse(accessToken, refreshToken, "Bearer",
                savedUser.getUserId(), savedUser.getFullName(), savedUser.getEmail(), studentRole.getName());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        String accessToken = jwtUtil.generateToken(userPrincipal);
        String refreshToken = jwtUtil.generateRefreshToken(userPrincipal);

        return new AuthResponse(accessToken, refreshToken, "Bearer",
                user.getUserId(), user.getFullName(), user.getEmail(), userPrincipal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(refreshToken, userDetails)) {
            String accessToken = jwtUtil.generateToken(userDetails);
            UserPrincipal userPrincipal = (UserPrincipal) userDetails;
            return new AuthResponse(accessToken, refreshToken, "Bearer",
                    userPrincipal.getUserId(), userPrincipal.getUser().getFullName(), userPrincipal.getUser().getEmail(),
                    userPrincipal.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        }
        throw new RuntimeException("Invalid refresh token");
    }
}