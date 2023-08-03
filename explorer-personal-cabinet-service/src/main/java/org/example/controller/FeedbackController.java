package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.CourseRatingCreateRequest;
import org.example.dto.feedback.ExplorerFeedbackCreateRequest;
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

    @PostMapping("course/{courseId}/keeper-feedback")
    @PreAuthorize("@roleService.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Send feedback for keeper", tags = "feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendFeedbackForKeeper(@PathVariable("courseId") Integer courseId,
                                                   @Valid @RequestBody ExplorerFeedbackCreateRequest feedback) {
        return ResponseEntity.ok(feedbackService.sendFeedbackForKeeper(courseId, feedback));
    }

    @PostMapping("course/{courseId}/course-feedback")
    @PreAuthorize("@roleService.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Send feedback for course", tags = "feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> rateCourse(@PathVariable("courseId") Integer courseId,
                                        @Valid @RequestBody CourseRatingCreateRequest request) {
        return ResponseEntity.ok(feedbackService.rateCourse(courseId, request));
    }
}
