package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.KeeperInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keeper-cabinet/")
@RequiredArgsConstructor
public class KeeperController {
    private final KeeperInformationService keeperInformationService;

    @GetMapping("info")
    @PreAuthorize("@RoleService.hasAnyAuthenticationRole(T(org.example.model.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get keeper information", tags = "personal cabinet", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Keeper information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getKeeperInformation() {
        return ResponseEntity.ok(keeperInformationService.getKeeperInformation());
    }
}
