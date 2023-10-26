package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.ExplorerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/person-app")
@RequiredArgsConstructor
public class ExplorerController {
    private final ExplorerService explorerService;

    @GetMapping("/explorer/{explorerId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find explorer by id", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorer",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorerById(@PathVariable Integer explorerId) {
        return ResponseEntity.ok(
                explorerService.findExplorerById(explorerId)
        );
    }

    @GetMapping("/explorers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find explorers by explorer id in", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorer(@RequestParam List<Integer> explorerIds) {
        return ResponseEntity.ok(
                explorerService.findExplorersByExplorerIdIn(explorerIds)
        );
    }


    @GetMapping("/explorer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find explorer(s)", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorer(s)",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorer(@RequestParam Integer personId,
                                          @RequestParam(required = false) Integer courseId) {
        if (courseId == null)
            return ResponseEntity.ok(explorerService.findExplorersByPersonId(personId));
        return ResponseEntity.ok(
                explorerService.findExplorerByPersonIdAndCourseId(personId, courseId)
        );
    }

    @GetMapping("/course/{courseId}/explorer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find explorers on course", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorersByCourseId(@PathVariable Integer courseId) {
        return ResponseEntity.ok(explorerService.findExplorersByCourseId(courseId));
    }

    @GetMapping("/people/explorers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find explorers", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorersByPersonIdIn(@RequestParam List<Integer> personIds) {
        return ResponseEntity.ok(explorerService.findExplorersByPersonIdIn(personIds));
    }

    @GetMapping("/course/explorers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find explorers", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorersByGroup_CourseIdIn(@RequestParam List<Integer> courseIds) {
        return ResponseEntity.ok(explorerService.findExplorersByGroup_CourseIdIn(courseIds));
    }

    @GetMapping("/explorers/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find all explorers", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested explorers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findAllExplorers() {
        return ResponseEntity.ok(explorerService.findExplorersWithCourseIds());
    }

    @DeleteMapping("/explorers/{explorerId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.isPersonExplorer(#explorerId)")
    @Operation(summary = "Delete explorer by id", tags = "explorer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Explorer deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> deleteExplorerById(@PathVariable Integer explorerId) {
        return ResponseEntity.ok(explorerService.deleteExplorerById(explorerId));
    }
}
