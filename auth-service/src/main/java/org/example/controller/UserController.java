package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.LoginRequest;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @PostMapping("login")
    @Operation(summary = "Log in", tags = "authentication")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Log in by username and password successful",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                   HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(loginRequest, response));
    }

    @PostMapping("logout")
    @Operation(summary = "Log out", tags = "authentication")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Log out successful",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> logout(HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(response));
    }
}
