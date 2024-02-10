package org.example.courseregistration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/course-registration-app/course-requests")
@RequiredArgsConstructor
public class CourseRegistrationRequestKeeperController {
    private final CourseRegistrationRequestKeeperService courseRegistrationRequestKeeperService;

    @GetMapping("/{requestId}/keepers")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.courseregistration.enums.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Get request keepers by request id", tags = "course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course registration request keepers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findCourseRegistrationRequestKeepersByRequestId(@AuthenticationPrincipal Long authenticatedPersonId,
                                                                             @PathVariable Long requestId) {
        return ResponseEntity.ok(
                courseRegistrationRequestKeeperService
                        .findCourseRegistrationRequestKeepersByRequestId(authenticatedPersonId, requestId)
        );
    }

    @GetMapping("/keepers")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.courseregistration.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get request keepers by request id", tags = "course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course registration request keepers",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                                        @AuthenticationPrincipal Long authenticatedPersonId,
                                                                                        @RequestParam List<Long> keeperIds) {
        return ResponseEntity.ok(
                courseRegistrationRequestKeeperService
                        .findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(
                                authorizationHeader, authenticatedPersonId, keeperIds
                        )
        );
    }
}
