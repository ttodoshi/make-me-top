package org.example.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.homework.dto.homeworkrequest.CreateHomeworkRequestDto;
import org.example.homework.service.ExplorerHomeworkRequestService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/homework-app")
@RequiredArgsConstructor
public class ExplorerHomeworkRequestController {
    private final ExplorerHomeworkRequestService explorerHomeworkRequestService;

    @PostMapping("/homeworks/{homeworkId}/homework-requests/")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.homework.enums.AuthenticationRoleType).EXPLORER)")
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
    public ResponseEntity<?> sendRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @AuthenticationPrincipal Long authenticatedPersonId,
                                         @PathVariable Long homeworkId,
                                         @Valid @RequestBody CreateHomeworkRequestDto request) {
        return ResponseEntity.ok(
                explorerHomeworkRequestService.sendHomeworkRequest(authorizationHeader, authenticatedPersonId, homeworkId, request)
        );
    }
}
