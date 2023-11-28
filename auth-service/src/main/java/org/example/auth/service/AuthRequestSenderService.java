package org.example.auth.service;

import org.example.auth.dto.auth.LoginRequestDto;
import org.example.auth.dto.mmtr.MmtrAuthResponseDto;

public interface AuthRequestSenderService {
    MmtrAuthResponseDto sendAuthenticateRequest(LoginRequestDto loginRequest);
}
