package org.example.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.person.service.implementations.ExplorerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/person-app")
@RequiredArgsConstructor
public class ExplorerController {
    private final ExplorerService explorerService;

    @DeleteMapping("/explorers/{explorerId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete explorer by id", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Explorer deleted"
            )
    })
    public ResponseEntity<?> deleteExplorerById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                @CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                                @PathVariable Long explorerId) {
        explorerService.deleteExplorerById(authorizationHeader, authentication, explorerId);
        return ResponseEntity.noContent().build();
    }
}
