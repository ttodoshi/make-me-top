package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.systemprogress.ProgressUpdateRequest;
import org.example.service.SystemProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/galaxy-app/")
@RequiredArgsConstructor
public class SystemProgressController {
    private final SystemProgressService systemProgressService;

    @PatchMapping("/system/{systemId}")
    @Secured({"ROLE_EXPLORER", "ROLE_KEEPER", "ROLE_BIG_BROTHER"})
    @Operation(summary = "Update system progress for current user", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "System progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateSystemsProgress(@PathVariable("systemId") Integer systemId,
                                                @RequestBody ProgressUpdateRequest updateRequest) {
        return ResponseEntity.ok(
                systemProgressService.updateSystemProgress(systemId, updateRequest));
    }
}
