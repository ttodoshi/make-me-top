package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CreateCourseRegistrationRequest;
import org.example.service.CourseRegistrationRequestService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/explorer-cabinet/course-request")
@RequiredArgsConstructor
public class CourseRegistrationRequestController {
    private final CourseRegistrationRequestService courseRegistrationRequestService;

    @PostMapping
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Send course registration request", tags = "course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course request sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendRequest(@Valid @RequestBody CreateCourseRegistrationRequest request,
                                         @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        courseRegistrationRequestService.setToken(token);
        return ResponseEntity.ok(courseRegistrationRequestService.sendRequest(request));
    }

    @PatchMapping("{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Cancel course registration request", tags = "course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course request canceled",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> cancelRequest(@PathVariable("requestId") Integer requestId) {
        return ResponseEntity.ok(courseRegistrationRequestService.cancelRequest(requestId));
    }
}
