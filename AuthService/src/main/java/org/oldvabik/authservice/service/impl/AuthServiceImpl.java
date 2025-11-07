package org.oldvabik.authservice.service.impl;

import org.oldvabik.authservice.dto.*;
import org.oldvabik.authservice.entity.Credential;
import org.oldvabik.authservice.entity.RefreshToken;
import org.oldvabik.authservice.exception.*;
import org.oldvabik.authservice.repository.CredentialRepository;
import org.oldvabik.authservice.repository.RefreshTokenRepository;
import org.oldvabik.authservice.security.JwtProvider;
import org.oldvabik.authservice.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final CredentialRepository credentialRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(CredentialRepository credentialRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           JwtProvider jwtProvider,
                           PasswordEncoder passwordEncoder) {
        this.credentialRepository = credentialRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        Credential user = credentialRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String access = jwtProvider.generateToken(user.getUsername(), user.getRole().name());
        String refresh = jwtProvider.generateRefreshToken(user.getUsername());

        refreshTokenRepository.deleteByUsername(user.getUsername());
        refreshTokenRepository.save(RefreshToken.builder()
                .username(user.getUsername())
                .token(refresh)
                .build());

        return new AuthResponse(access, refresh);
    }

    @Override
    public boolean validate(ValidateTokenRequest token) {
        boolean valid = jwtProvider.validateToken(token);
        if (!valid) {
            throw new TokenValidationException("Invalid or expired token");
        }
        return true;
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        String username = token.getUsername();
        Credential user = credentialRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!jwtProvider.validateRawToken(request.getRefreshToken())) {
            throw new TokenValidationException("Expired or invalid refresh token");
        }

        String newAccess = jwtProvider.generateToken(username, user.getRole().name());
        String newRefresh = jwtProvider.generateRefreshToken(username);

        token.setToken(newRefresh);
        refreshTokenRepository.save(token);

        return new AuthResponse(newAccess, newRefresh);
    }

    @Override
    public void register(RegisterRequest request) {
        if (credentialRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AlreadyExistsException("User already exists");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }

        Credential credential = Credential.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        credentialRepository.save(credential);
    }
}
