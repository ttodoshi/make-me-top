package org.example.person.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.person.dto.keeper.CreateKeeperDto;
import org.example.person.service.KeeperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/person-app")
@RequiredArgsConstructor
public class KeeperController {
    private final KeeperService keeperService;

    @PostMapping("/courses/{courseId}/keepers")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.person.enums.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Add keeper on course", tags = "keeper")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully added keeper",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setKeeperToCourse(@PathVariable Long courseId,
                                               @Valid @RequestBody CreateKeeperDto keeper) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperService.setKeeperToCourse(courseId, keeper)
                );
    }
}
