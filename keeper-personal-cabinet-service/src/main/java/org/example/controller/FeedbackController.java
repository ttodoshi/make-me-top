package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.feedback.KeeperFeedbackCreateRequest;
import org.example.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/keeper-cabinet/")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("course/{courseId}/keeper-feedback")
    @PreAuthorize("@roleService.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Send feedback for explorer", tags = "feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendFeedbackForExplorer(@PathVariable("courseId") Integer courseId,
                                                     @Valid @RequestBody KeeperFeedbackCreateRequest feedback) {
        return ResponseEntity.ok(feedbackService.sendFeedbackForExplorer(courseId, feedback));
    }
}
