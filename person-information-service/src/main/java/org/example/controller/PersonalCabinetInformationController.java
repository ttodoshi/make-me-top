package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.ExplorerCabinetInformationService;
import org.example.service.KeeperCabinetInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info/")
@RequiredArgsConstructor
public class PersonalCabinetInformationController {
    private final ExplorerCabinetInformationService explorerCabinetInformationService;
    private final KeeperCabinetInformationService keeperCabinetInformationService;

    @GetMapping("explorer-cabinet")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Get explorer personal cabinet information", tags = "personal cabinet", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getExplorerCabinetInformation() {
        return ResponseEntity.ok(explorerCabinetInformationService.getExplorerCabinetInformation());
    }

    @GetMapping("keeper-cabinet")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get keeper personal cabinet information", tags = "personal cabinet", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getKeeperCabinetInformation() {
        return ResponseEntity.ok(keeperCabinetInformationService.getKeeperCabinetInformation());
    }
}
