package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.InformationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/info/")
@RequiredArgsConstructor
public class InfoController {
    private final InformationService informationService;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get information", tags = "personal cabinet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token,
                                            HttpServletRequest request) {
        return informationService.getInformation(token, request);
    }
}
