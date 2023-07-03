package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keeper/")
@RequiredArgsConstructor
public class KeeperController {

    @GetMapping
    @PreAuthorize("@RoleService.hasAnyAuthenticationRole(T(org.example.model.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get keeper information", tags = "personal cabinet")
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
        return ResponseEntity.ok("");
    }
}
