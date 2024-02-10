package org.example.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.person.exception.role.RoleNotAvailableException;
import org.example.person.service.api.profile.ExplorerProfileInformationService;
import org.example.person.service.api.profile.ExplorerPublicInformationService;
import org.example.person.service.api.profile.KeeperProfileInformationService;
import org.example.person.service.api.profile.KeeperPublicInformationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/person-app/people")
@RequiredArgsConstructor
public class PersonInformationController {
    private final ExplorerProfileInformationService explorerProfileInformationService;
    private final KeeperProfileInformationService keeperProfileInformationService;
    private final ExplorerPublicInformationService explorerPublicInformationService;
    private final KeeperPublicInformationService keeperPublicInformationService;

    @GetMapping("/explorer-profile")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.person.enums.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Get explorer profile information", tags = "profile")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getExplorerProfileInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                           @AuthenticationPrincipal Long authenticatedPersonId) {
        return ResponseEntity.ok(
                explorerProfileInformationService.getExplorerProfileInformation(authorizationHeader, authenticatedPersonId)
        );
    }

    @GetMapping("/keeper-profile")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.person.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get keeper profile information", tags = "profile")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getKeeperProfileInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                         @AuthenticationPrincipal Long authenticatedPersonId) {
        return ResponseEntity.ok(
                keeperProfileInformationService.getKeeperProfileInformation(authorizationHeader, authenticatedPersonId)
        );
    }

    @GetMapping("/{personId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get public information", tags = "public")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getPublicInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                  @CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                                  @PathVariable Long personId,
                                                  @RequestParam String as) {
        if (as.equals("explorer"))
            return ResponseEntity.ok(
                    explorerPublicInformationService
                            .getExplorerPublicInformation(authorizationHeader, authentication, personId)
            );
        else if (as.equals("keeper"))
            return ResponseEntity.ok(
                    keeperPublicInformationService
                            .getKeeperPublicInformation(authorizationHeader, personId)
            );
        else throw new RoleNotAvailableException();
    }
}
