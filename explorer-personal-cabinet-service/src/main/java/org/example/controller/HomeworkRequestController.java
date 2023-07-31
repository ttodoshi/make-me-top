package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.homework.CreateHomeworkRequest;
import org.example.service.HomeworkRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/explorer-cabinet")
@RequiredArgsConstructor
public class HomeworkRequestController {
    private final HomeworkRequestService homeworkRequestService;

    @PostMapping("homework/{homeworkId}")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Send homework review request", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework request",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendRequest(@PathVariable("homeworkId") Integer homeworkId,
                                         @Valid @RequestBody CreateHomeworkRequest request) {
        return ResponseEntity.ok(homeworkRequestService.sendHomeworkRequest(homeworkId, request));
    }

    @GetMapping("theme/{themeId}/homework")
    @PreAuthorize("@roleService.hasAnyCourseRoleByThemeId(#themeId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Get opened requests by themeId", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework requests",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendRequest(@PathVariable("themeId") Integer themeId) {
        return ResponseEntity.ok(homeworkRequestService.getHomeworkRequests(themeId));
    }
}
