package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.InfoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keeper-cabinet/info/")
@RequiredArgsConstructor
public class InfoController {
    private final InfoService infoService;

    @GetMapping
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get keeper personal cabinet info", tags = "info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Personal cabinet info",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getKeeperPersonalCabinetInfo() {
        return ResponseEntity.ok(infoService.getKeeperPersonalCabinetInfo());
    }
}
