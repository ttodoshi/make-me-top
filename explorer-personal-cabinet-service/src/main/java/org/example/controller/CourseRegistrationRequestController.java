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

@RestController
@RequestMapping("/explorer-cabinet/course-request")
@RequiredArgsConstructor
public class CourseRegistrationRequestController {
    private final CourseRegistrationRequestService courseRegistrationRequestService;

    @PostMapping
    @PreAuthorize("@RoleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Send course registration request", tags = "course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course request",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendRequest(@RequestBody CreateCourseRegistrationRequest request) {
        return ResponseEntity.ok(courseRegistrationRequestService.sendRequest(request));
    }
}
