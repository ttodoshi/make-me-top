package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.CourseRegistrationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/course-registration-app/course-requests")
@RequiredArgsConstructor
public class CourseRegistrationRequestController {
    private final CourseRegistrationRequestService courseRegistrationRequestService;

    @GetMapping("/processing")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).EXPLORER)")
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
    public ResponseEntity<?> findProcessingCourseRegistrationRequestByPersonId() {
        return ResponseEntity.ok(
                courseRegistrationRequestService
                        .findProcessingCourseRegistrationRequestByPersonId()
        );
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()") // TODO
//    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).EXPLORER) && " +
//            "@roleService.hasAnyCourseRoleByRequestIds(#requestIds, T(org.example.config.security.role.CourseRoleType).EXPLORER)) ||" +
//            "(@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) && " +
//            "@roleService.hasAnyCourseRoleByRequestIds(#requestIds, T(org.example.config.security.role.CourseRoleType).KEEPER))")
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
    public ResponseEntity<?> findCourseRegistrationRequestsByRequestIdIn(@RequestParam List<Integer> requestIds) {
        return ResponseEntity.ok(
                courseRegistrationRequestService
                        .findCourseRegistrationRequestsByRequestIdIn(requestIds)
        );
    }
}
