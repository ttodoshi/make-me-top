package org.example.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.person.exception.classes.role.RoleNotAvailableException;
import org.example.person.service.ExplorerProfileInformationService;
import org.example.person.service.ExplorerPublicInformationService;
import org.example.person.service.KeeperProfileInformationService;
import org.example.person.service.KeeperPublicInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.person.enums.AuthenticationRoleType).EXPLORER)")
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
    public ResponseEntity<?> getExplorerProfileInformation() {
        return ResponseEntity.ok(
                explorerProfileInformationService.getExplorerProfileInformation()
        );
    }

    @GetMapping("/keeper-profile")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.person.enums.AuthenticationRoleType).KEEPER)")
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
    public ResponseEntity<?> getKeeperProfileInformation() {
        return ResponseEntity.ok(
                keeperProfileInformationService.getKeeperProfileInformation()
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
    public ResponseEntity<?> getPublicInformation(@PathVariable Long personId,
                                                  @RequestParam String as) {
        if (as.equals("explorer"))
            return ResponseEntity.ok(
                    explorerPublicInformationService
                            .getExplorerPublicInformation(personId)
            );
        else if (as.equals("keeper"))
            return ResponseEntity.ok(
                    keeperPublicInformationService
                            .getKeeperPublicInformation(personId)
            );
        else throw new RoleNotAvailableException();
    }
}
