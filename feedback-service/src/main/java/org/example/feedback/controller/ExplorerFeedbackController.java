package org.example.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.feedback.CreateCourseRatingDto;
import org.example.feedback.dto.feedback.CreateExplorerFeedbackDto;
import org.example.feedback.service.ExplorerFeedbackService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> findExplorerFeedbacksByIdIn(@RequestParam List<Long> feedbackIds) {
        return ResponseEntity.ok(
                explorerFeedbackService.findExplorerFeedbacksByIdIn(feedbackIds)
        );
    }

    @GetMapping("/explorer-feedbacks/offers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get explorer feedback offers by keeper id in", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested offers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorerFeedbackOffersByKeeperIdIn(@RequestParam List<Long> keeperIds) {
        return ResponseEntity.ok(
                explorerFeedbackService.findExplorerFeedbackOffersByKeeperIdIn(keeperIds)
        );
    }

    @GetMapping("/explorer-feedbacks/offers/valid")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get explorer feedback offers by explorer id in", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested offers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(@RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                explorerFeedbackService
                        .findExplorerFeedbackOffersByExplorerIdInAndOfferValidIsTrue(explorerIds)
        );
    }

    @GetMapping("/course-feedbacks/offers/valid")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get course rating offers by keeper id in", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested offers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(@RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                explorerFeedbackService
                        .findCourseRatingOffersByExplorerIdInAndOfferValidIsTrue(explorerIds)
        );
    }

    @PostMapping("/explorer-feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.feedback.enums.AuthenticationRoleType).EXPLORER)")
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
    public ResponseEntity<?> sendFeedbackForKeeper(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                   @AuthenticationPrincipal Long authenticatedPersonId,
                                                   @Valid @RequestBody CreateExplorerFeedbackDto feedback) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        explorerFeedbackService.sendFeedbackForKeeper(authorizationHeader, authenticatedPersonId, feedback)
                );
    }

    @PostMapping("/course-feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.feedback.enums.AuthenticationRoleType).EXPLORER)")
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
    public ResponseEntity<?> rateCourse(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                        @AuthenticationPrincipal Long authenticatedPersonId,
                                        @Valid @RequestBody CreateCourseRatingDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        explorerFeedbackService.rateCourse(authorizationHeader, authenticatedPersonId, request)
                );
    }

    @PatchMapping("/explorer-feedbacks/offers/{explorerId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.feedback.enums.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Close explorer feedback offer", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer closed",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> closeExplorerFeedbackOffer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                        @AuthenticationPrincipal Long authenticatedPersonId,
                                                        @PathVariable Long explorerId) {
        return ResponseEntity.ok(
                explorerFeedbackService.closeExplorerFeedbackOffer(authorizationHeader, authenticatedPersonId, explorerId)
        );
    }

    @PatchMapping("/course-feedbacks/offers/{explorerId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.feedback.enums.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Close course rating offer", tags = "explorer feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer closed",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> closeCourseRatingOffer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                    @AuthenticationPrincipal Long authenticatedPersonId,
                                                    @PathVariable Long explorerId) {
        return ResponseEntity.ok(
                explorerFeedbackService.closeCourseRatingOffer(authorizationHeader, authenticatedPersonId, explorerId)
        );
    }
}
