package org.example.courseregistration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.courseregistration.service.CourseRegistrationRequestService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/course-registration-app/course-requests")
@RequiredArgsConstructor
public class CourseRegistrationRequestController {
    private final CourseRegistrationRequestService courseRegistrationRequestService;

    @GetMapping("/processing")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.courseregistration.enums.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Get processing request for person", tags = "course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course registration request",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findProcessingCourseRegistrationRequestByPersonId(@AuthenticationPrincipal Long authenticatedPersonId) {
        return ResponseEntity.ok(
                courseRegistrationRequestService
                        .findProcessingCourseRegistrationRequestByPersonId(authenticatedPersonId)
        );
    }

    @GetMapping
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.courseregistration.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get course registration requests by request ids", tags = "course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course registration requests",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findCourseRegistrationRequestsByRequestIdIn(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                         @AuthenticationPrincipal Long authenticatedPersonId,
                                                                         @RequestParam List<Long> requestIds) {
        return ResponseEntity.ok(
                courseRegistrationRequestService
                        .findCourseRegistrationRequestsByRequestIdIn(authorizationHeader, authenticatedPersonId, requestIds)
        );
    }
}
