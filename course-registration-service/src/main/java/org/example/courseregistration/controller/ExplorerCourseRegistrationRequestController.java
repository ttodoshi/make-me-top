package org.example.courseregistration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateCourseRegistrationRequestDto;
import org.example.courseregistration.service.ExplorerCourseRegistrationRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/course-registration-app/course-requests")
@RequiredArgsConstructor
@Validated
public class ExplorerCourseRegistrationRequestController {
    private final ExplorerCourseRegistrationRequestService explorerCourseRegistrationRequestService;

    @PostMapping
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Send course registration request", tags = "explorer course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Course request sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendRequest(@Valid @RequestBody CreateCourseRegistrationRequestDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        explorerCourseRegistrationRequestService.sendRequest(request)
                );
    }

    @DeleteMapping("/{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Cancel course registration request", tags = "explorer course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course request canceled",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> cancelRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(
                explorerCourseRegistrationRequestService.cancelRequest(requestId)
        );
    }
}
