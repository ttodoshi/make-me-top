package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.InformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info/keeper/")
@RequiredArgsConstructor
public class KeeperInformationController {
    private final InformationService informationService;

    @GetMapping("{personId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get information", tags = "public info")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Keeper information",
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
                    description = "Keeper rating",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> getKeeperRating(@PathVariable("personId") Integer personId) {
        return ResponseEntity.ok(informationService.getKeeperRating(personId));
    }
}
