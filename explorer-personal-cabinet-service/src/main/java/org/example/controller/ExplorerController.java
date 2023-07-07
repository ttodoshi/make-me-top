package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.ExplorerInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/explorer/")
@RequiredArgsConstructor
public class ExplorerController {
    private final ExplorerInformationService explorerInformationService;

    @GetMapping
    @PreAuthorize("@RoleService.hasAnyAuthenticationRole(T(org.example.model.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Get explorer information", tags = "personal cabinet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Explorer information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getExplorerInformation() {
        return ResponseEntity.ok(explorerInformationService.getExplorerInformation());
    }
}
