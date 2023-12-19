package org.example.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.person.service.ExplorerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/person-app")
@RequiredArgsConstructor
public class ExplorerController {
    private final ExplorerService explorerService;

    @DeleteMapping("/explorers/{explorerId}")
    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.person.enums.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.isPersonExplorer(#explorerId)) || " +
            "(@roleService.hasAnyAuthenticationRole(T(org.example.person.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.isKeeperForExplorer(#explorerId))")
    @Operation(summary = "Delete explorer by id", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Explorer deleted"
            )
    })
    public ResponseEntity<?> deleteExplorerById(@PathVariable Long explorerId) {
        explorerService.deleteExplorerById(explorerId);
        return ResponseEntity.noContent().build();
    }
}
