package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.CreateKeeperDto;
import org.example.service.KeeperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/person-app")
@RequiredArgsConstructor
public class KeeperController {
    private final KeeperService keeperService;

    @GetMapping("/keeper/{keeperId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find keeper by keeper id", tags = "keeper")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested keeper",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeeperByKeeperId(@PathVariable Integer keeperId) {
        return ResponseEntity.ok(
                keeperService.findKeeperByKeeperId(keeperId)
        );
    }

    @GetMapping("/keeper")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find keeper(s)", tags = "keeper")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested keeper(s)",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeeper(@RequestParam Integer personId,
                                        @RequestParam(required = false) Integer courseId) {
        if (courseId == null)
            return ResponseEntity.ok(keeperService.findKeepersByPersonId(personId));
        return ResponseEntity.ok(
                keeperService.findKeeperByPersonIdAndCourseId(personId, courseId)
        );
    }

    @GetMapping("/course/{courseId}/keeper")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find keepers course id", tags = "keeper")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested keepers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeepersByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(
                keeperService.findKeepersByCourseId(courseId)
        );
    }

    @GetMapping("/people/keepers")
    @Operation(summary = "Find keepers", tags = "keeper")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested keepers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeepersByPersonIdIn(@RequestParam List<Integer> personIds) {
        return ResponseEntity.ok(
                keeperService.findKeepersByPersonIdIn(personIds)
        );
    }

    @GetMapping("/keepers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find keepers", tags = "keeper")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested keepers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeepers(@RequestParam List<Integer> keeperIds) {
        return ResponseEntity.ok(
                keeperService.findKeepersByKeeperIdIn(keeperIds)
        );
    }

    @GetMapping("/keepers/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find all keepers", tags = "keeper")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested keepers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findAllKeepers() {
        return ResponseEntity.ok(keeperService.findKeepersWithCourseIds());
    }

    @PostMapping("/course/{courseId}/keeper")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
    @Operation(summary = "Add keeper on course", tags = "keeper")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added keeper",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setKeeperToCourse(@PathVariable Integer courseId,
                                               @Valid @RequestBody CreateKeeperDto createKeeper) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperService.setKeeperToCourse(courseId, createKeeper)
                );
    }
}
