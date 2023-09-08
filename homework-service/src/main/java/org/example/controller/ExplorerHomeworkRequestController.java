package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.homework.CreateHomeworkRequestDto;
import org.example.service.ExplorerHomeworkRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/homework-app")
@RequiredArgsConstructor
public class ExplorerHomeworkRequestController {
    private final ExplorerHomeworkRequestService explorerHomeworkRequestService;

    @PostMapping("homework/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER) &&" +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Send homework review request", tags = "explorer homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework review request sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendRequest(@PathVariable("homeworkId") Integer homeworkId,
                                         @Valid @RequestBody CreateHomeworkRequestDto request) {
        return ResponseEntity.ok(explorerHomeworkRequestService.sendHomeworkRequest(homeworkId, request));
    }

    @GetMapping("theme/{themeId}/homework")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Get opened requests by theme id", tags = "explorer homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Opened homework requests by theme id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getHomeworkRequests(@PathVariable("themeId") Integer themeId) {
        return ResponseEntity.ok(explorerHomeworkRequestService.getHomeworkRequests(themeId));
    }
}
