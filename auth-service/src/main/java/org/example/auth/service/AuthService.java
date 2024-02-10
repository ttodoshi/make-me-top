package org.example.auth.service;

import org.example.auth.dto.auth.AuthResponseDto;
import org.example.auth.dto.auth.LoginRequestDto;
import org.example.auth.dto.message.MessageDto;

public interface AuthService {
    AuthResponseDto login(LoginRequestDto loginRequest);

    AuthResponseDto refresh(String refreshTokenValue);

    MessageDto logout(String refreshToken);
}
