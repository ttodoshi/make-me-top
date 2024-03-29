package org.example.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.homework.dto.homeworkmark.CreateHomeworkMarkDto;
import org.example.homework.dto.homeworkrequest.CreateHomeworkRequestFeedbackDto;
import org.example.homework.service.KeeperHomeworkRequestService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/homework-app")
@RequiredArgsConstructor
public class KeeperHomeworkRequestController {
    private final KeeperHomeworkRequestService keeperHomeworkRequestService;

    @PostMapping("/homework-requests/{requestId}/marks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Set homework accepted", tags = "keeper homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Homework mark set",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setHomeworkMark(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                             @AuthenticationPrincipal Long authenticatedPersonId,
                                             @PathVariable Long requestId,
                                             @Valid @RequestBody CreateHomeworkMarkDto mark) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperHomeworkRequestService.setHomeworkMark(authorizationHeader, authenticatedPersonId, requestId, mark)
                );
    }

    @PostMapping("/homework-requests/{requestId}/feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Send homework request feedback", tags = "keeper homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Homework request feedback sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendHomeworkFeedback(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                  @AuthenticationPrincipal Long authenticatedPersonId,
                                                  @PathVariable Long requestId,
                                                  @Valid @RequestBody CreateHomeworkRequestFeedbackDto feedback) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperHomeworkRequestService.sendHomeworkRequestFeedback(authorizationHeader, authenticatedPersonId, requestId, feedback)
                );
    }
}
