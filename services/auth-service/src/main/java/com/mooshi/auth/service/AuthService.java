package com.mooshi.auth.service;

import com.mooshi.api.dto.LoginRequest;
import com.mooshi.api.dto.RegisterRequest;
import com.mooshi.api.dto.TokenResponse;
import com.mooshi.auth.dto.UserResponse;
import com.mooshi.auth.event.EventPublisher;
import com.mooshi.auth.model.RefreshToken;
import com.mooshi.auth.model.Role;
import com.mooshi.auth.model.User;
import com.mooshi.auth.repository.RefreshTokenRepository;
import com.mooshi.auth.repository.RoleRepository;
import com.mooshi.auth.repository.UserRepository;
import com.mooshi.auth.security.JwtService;
import com.mooshi.common.exception.DuplicateResourceException;
import com.mooshi.common.exception.UnauthorizedException;
import com.mooshi.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EventPublisher eventPublisher;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered");
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
            .orElseThrow(() -> new IllegalStateException("Default role not found"));

        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .roles(Set.of(customerRole))
            .build();

        user = userRepository.save(user);

        eventPublisher.publishUserRegistered(new UserRegisteredEvent(
            null, user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), null
        ));

        return toResponse(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }

        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .toList();

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtService.generateRefreshToken();

        refreshTokenRepository.revokeAllByUserId(user.getId());
        refreshTokenRepository.save(RefreshToken.builder()
            .userId(user.getId())
            .token(refreshToken)
            .expiresAt(Instant.now().plusMillis(jwtService.getRefreshExpiration()))
            .build());

        return TokenResponse.of(accessToken, refreshToken, jwtService.getExpiration() / 1000);
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
            .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        User user = userRepository.findById(refreshToken.getUserId())
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .toList();

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roles);
        String newRefreshToken = jwtService.generateRefreshToken();

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        refreshTokenRepository.save(RefreshToken.builder()
            .userId(user.getId())
            .token(newRefreshToken)
            .expiresAt(Instant.now().plusMillis(jwtService.getRefreshExpiration()))
            .build());

        return TokenResponse.of(newAccessToken, newRefreshToken, jwtService.getExpiration() / 1000);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(), user.getEmail(), user.getFirstName(),
            user.getLastName(), user.isEnabled(), user.getCreatedAt()
        );
    }
}
