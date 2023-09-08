package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.CreateCourseRatingDto;
import org.example.dto.feedback.CreateExplorerFeedbackDto;
import org.example.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/explorer-cabinet/")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("course/{courseId}/explorer-feedback")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Send feedback for keeper (rating from 1 to 5)", tags = "feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendFeedbackForKeeper(@PathVariable("courseId") Integer courseId,
                                                   @Valid @RequestBody CreateExplorerFeedbackDto feedback) {
        return ResponseEntity.ok(feedbackService.sendFeedbackForKeeper(courseId, feedback));
    }

    @PostMapping("course/{courseId}/course-feedback")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Set rating from 1 to 5 for course", tags = "feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rating set",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> rateCourse(@PathVariable("courseId") Integer courseId,
                                        @Valid @RequestBody CreateCourseRatingDto request) {
        return ResponseEntity.ok(feedbackService.rateCourse(courseId, request));
    }
}
