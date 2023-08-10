package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.ExplorerService;
import org.example.service.InformationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Period;

@RestController
@RequestMapping("/info/explorer/")
@RequiredArgsConstructor
public class ExplorerInformationController {
    private final InformationService informationService;
    private final ExplorerService explorerService;

    @GetMapping("{personId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get explorer public information", tags = "public info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorer information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getInformation(@PathVariable Integer personId) {
        return ResponseEntity.ok(informationService.getExplorerPublicInformation(personId));
    }

    @GetMapping("{personId}/rating")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get explorer rating", tags = "public info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorer rating",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> getExplorerRating(@PathVariable("personId") Integer personId) {
        return ResponseEntity.ok(informationService.getExplorerRating(personId));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get explorers", tags = "public info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Explorers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getKeepers(@RequestParam(required = false) String sort,
                                        @RequestParam(required = false) Period period,
                                        @RequestParam(required = false) Integer systemId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        explorerService.setToken(token);
        return ResponseEntity.ok(explorerService.getExplorers(sort, period, systemId));
    }
}
