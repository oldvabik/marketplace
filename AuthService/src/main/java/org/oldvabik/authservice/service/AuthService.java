package org.oldvabik.authservice.service;

import org.oldvabik.authservice.dto.*;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    boolean validate(ValidateTokenRequest token);

    AuthResponse refresh(RefreshTokenRequest request);

    void register(RegisterRequest request);
}
