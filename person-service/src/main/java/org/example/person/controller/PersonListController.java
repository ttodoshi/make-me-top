package org.example.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.person.exception.role.RoleNotAvailableException;
import org.example.person.service.api.profile.ExplorerListService;
import org.example.person.service.api.profile.KeeperListService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getPersonList(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                           @RequestParam String as, @RequestParam Integer page, @RequestParam Integer size) {
        if (as.equals("explorer"))
            return ResponseEntity.ok(
                    explorerListService.getExplorers(authorizationHeader, page, size)
            );
        else if (as.equals("keeper"))
            return ResponseEntity.ok(
                    keeperListService.getKeepers(authorizationHeader, page, size)
            );
        else throw new RoleNotAvailableException();
    }
}
