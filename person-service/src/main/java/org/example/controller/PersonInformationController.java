package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.exception.classes.roleEX.RoleNotAvailableException;
import org.example.service.ExplorerProfileInformationService;
import org.example.service.ExplorerPublicInformationService;
import org.example.service.KeeperProfileInformationService;
import org.example.service.KeeperPublicInformationService;
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
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).EXPLORER)")
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
        return ResponseEntity.ok(explorerProfileInformationService.getExplorerCabinetInformation());
    }

    @GetMapping("/keeper-profile")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER)")
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
        return ResponseEntity.ok(keeperProfileInformationService.getKeeperCabinetInformation());
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
    public ResponseEntity<?> getPublicInformation(@PathVariable Integer personId,
                                                  @RequestParam String as) {
        if (as.equals("explorer"))
            return ResponseEntity.ok(explorerPublicInformationService.getExplorerPublicInformation(personId));
        else if (as.equals("keeper"))
            return ResponseEntity.ok(keeperPublicInformationService.getKeeperPublicInformation(personId));
        else throw new RoleNotAvailableException();
    }
}
