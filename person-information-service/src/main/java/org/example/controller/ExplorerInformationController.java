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
@RequestMapping("/info/explorer/")
@RequiredArgsConstructor
public class ExplorerInformationController {
    private final InformationService informationService;

    @GetMapping("{personId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get information", tags = "personal cabinet")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Explorer information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getInformation(@PathVariable Integer personId) {
        return ResponseEntity.ok(informationService.getExplorerPublicInformation(personId));
    }
}
