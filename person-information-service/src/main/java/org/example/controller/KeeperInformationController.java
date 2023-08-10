package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.InformationService;
import org.example.service.KeeperService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info/keeper/")
@RequiredArgsConstructor
public class KeeperInformationController {
    private final InformationService informationService;
    private final KeeperService keeperService;

    @GetMapping("{personId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get keeper public information", tags = "public info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested keeper information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getInformation(@PathVariable Integer personId) {
        return ResponseEntity.ok(informationService.getKeeperPublicInformation(personId));
    }

    @GetMapping("{personId}/rating")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get keeper rating", tags = "public info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested keeper rating",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> getKeeperRating(@PathVariable("personId") Integer personId) {
        return ResponseEntity.ok(informationService.getKeeperRating(personId));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get keepers", tags = "public info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Keepers",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> getKeepers(@RequestParam(required = false) String sort,
                                        @RequestParam(required = false) Integer galaxyId,
                                        @RequestParam(required = false) Integer systemId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        keeperService.setToken(token);
        return ResponseEntity.ok(keeperService.getKeepers(sort, galaxyId, systemId));
    }
}
