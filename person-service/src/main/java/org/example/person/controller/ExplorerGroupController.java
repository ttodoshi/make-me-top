package org.example.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.person.exception.explorer.ExplorerGroupNotFoundException;
import org.example.person.service.api.progress.CourseProgressService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/person-app")
@RequiredArgsConstructor
public class ExplorerGroupController {
    private final CourseProgressService courseProgressService;

    @GetMapping("/groups/current")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.person.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get current keeper group", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Explorer deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCurrentGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                             @AuthenticationPrincipal Long authenticatedPersonId) {
        return ResponseEntity.ok(
                courseProgressService.getCurrentGroup(authorizationHeader, authenticatedPersonId)
                        .orElseThrow(ExplorerGroupNotFoundException::new)
        );
    }
}
