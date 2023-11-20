package org.example.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.person.exception.classes.role.RoleNotAvailableException;
import org.example.person.service.ExplorerListService;
import org.example.person.service.KeeperListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/person-app/people")
@RequiredArgsConstructor
public class PersonListController {
    private final ExplorerListService explorerListService;
    private final KeeperListService keeperListService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get person list", tags = "public")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested information",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getPersonList(@RequestParam String as) {
        if (as.equals("explorer"))
            return ResponseEntity.ok(explorerListService.getExplorers());
        else if (as.equals("keeper"))
            return ResponseEntity.ok(keeperListService.getKeepers());
        else throw new RoleNotAvailableException();
    }
}
