package org.example.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.feedback.dto.feedback.CreateKeeperFeedbackDto;
import org.example.feedback.service.KeeperFeedbackService;
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
public class KeeperFeedbackController {
    private final KeeperFeedbackService keeperFeedbackService;

    @GetMapping("/keeper-feedbacks")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get keeper feedbacks by explorer id in", tags = "keeper feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested feedbacks",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeeperFeedbacksByExplorerIdIn(@RequestParam List<Long> feedbackIds) {
        return ResponseEntity.ok(
                keeperFeedbackService.findKeeperFeedbacksByIdIn(feedbackIds)
        );
    }

    @GetMapping("/keeper-feedbacks/offers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get keeper feedback offers by explorer id in", tags = "keeper feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested offers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeeperFeedbackOffersByExplorerIdIn(@RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                keeperFeedbackService.findKeeperFeedbackOffersByExplorerIdIn(explorerIds)
        );
    }

    @GetMapping("/keeper-feedbacks/offers/valid")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get keeper feedback offers by explorer id in", tags = "keeper feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested offers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(@RequestParam List<Long> explorerIds) {
        return ResponseEntity.ok(
                keeperFeedbackService.findKeeperFeedbackOffersByExplorerIdInAndOfferValidIsTrue(explorerIds)
        );
    }

    @PostMapping("/keeper-feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.feedback.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Send feedback for explorer", tags = "keeper feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Feedback sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendFeedbackForExplorer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                     @AuthenticationPrincipal Long authenticatedPersonId,
                                                     @Valid @RequestBody CreateKeeperFeedbackDto feedback) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperFeedbackService.sendFeedbackForExplorer(authorizationHeader, authenticatedPersonId, feedback)
                );
    }

    @PatchMapping("/keeper-feedbacks/offers/{explorerId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.feedback.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Close keeper feedback offer", tags = "keeper feedback")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer closed",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> closeKeeperFeedbackOffer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @AuthenticationPrincipal Long authenticatedPersonId,
                                                      @PathVariable Long explorerId) {
        return ResponseEntity.ok(
                keeperFeedbackService.closeKeeperFeedbackOffer(authorizationHeader, authenticatedPersonId, explorerId)
        );
    }
}
