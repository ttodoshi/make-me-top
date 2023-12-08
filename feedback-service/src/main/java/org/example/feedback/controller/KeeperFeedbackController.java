package org.example.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.service.KeeperFeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback-app")
@RequiredArgsConstructor
public class KeeperFeedbackController {
    private final KeeperFeedbackService keeperFeedbackService;

    @GetMapping("/keeper-feedbacks")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get keeper feedbacks by explorer id in", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested feedbacks",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeeperFeedbacksByExplorerIdIn(@RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                keeperFeedbackService.findKeeperFeedbacksByExplorerIdIn(explorerIds)
        );
    }

    @PostMapping("/courses/{courseId}/keeper-feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.feedback.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.feedback.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Send feedback for explorer", tags = "keeper feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Feedback sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendFeedbackForExplorer(@PathVariable Long courseId,
                                                     @Valid @RequestBody CreateKeeperFeedbackDto feedback) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperFeedbackService.sendFeedbackForExplorer(courseId, feedback)
                );
    }
}
