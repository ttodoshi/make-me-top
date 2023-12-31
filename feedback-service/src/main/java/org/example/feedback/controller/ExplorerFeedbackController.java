package org.example.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.service.ExplorerFeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback-app")
@RequiredArgsConstructor
public class ExplorerFeedbackController {
    private final ExplorerFeedbackService explorerFeedbackService;

    @GetMapping("/explorer-feedbacks")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get explorer feedbacks by keeper id in", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested feedbacks",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorerFeedbacksByKeeperIdIn(@RequestParam List<Long> keeperIds) {
        return ResponseEntity.ok(
                explorerFeedbackService.findExplorerFeedbacksByKeeperIdIn(keeperIds)
        );
    }

    @PostMapping("/courses/{courseId}/explorer-feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.feedback.enums.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.feedback.enums.CourseRoleType).EXPLORER)")
    @Operation(summary = "Send feedback for keeper (rating from 1 to 5)", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Feedback sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendFeedbackForKeeper(@PathVariable Long courseId,
                                                   @Valid @RequestBody CreateExplorerFeedbackDto feedback) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        explorerFeedbackService.sendFeedbackForKeeper(courseId, feedback)
                );
    }

    @PostMapping("/courses/{courseId}/course-feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.feedback.enums.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.feedback.enums.CourseRoleType).EXPLORER)")
    @Operation(summary = "Set rating from 1 to 5 for course", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rating set",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> rateCourse(@PathVariable Long courseId,
                                        @Valid @RequestBody CreateCourseRatingDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        explorerFeedbackService.rateCourse(courseId, request)
                );
    }
}
