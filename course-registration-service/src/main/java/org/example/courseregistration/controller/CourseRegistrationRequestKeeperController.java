package org.example.courseregistration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.courseregistration.service.CourseRegistrationRequestKeeperService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/course-registration-app/course-requests")
@RequiredArgsConstructor
public class CourseRegistrationRequestKeeperController {
    private final CourseRegistrationRequestKeeperService courseRegistrationRequestKeeperService;

    @GetMapping("/{requestId}/keeper")
    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.isPersonInRequest(#requestId)) ||" +
            "(@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByRequestId(#requestId, T(org.example.courseregistration.enums.CourseRoleType).KEEPER))")
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
    public ResponseEntity<?> findCourseRegistrationRequestKeepersByRequestId(@PathVariable Integer requestId) {
        return ResponseEntity.ok(
                courseRegistrationRequestKeeperService
                        .findCourseRegistrationRequestKeepersByRequestId(requestId)
        );
    }

    @GetMapping("/keeper")
    @PreAuthorize("isAuthenticated()") // TODO
//    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.enums.AuthenticationRoleType).KEEPER) && " +
//            "@roleService.isPersonKeepers(#keeperIds)")
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
    public ResponseEntity<?> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(@RequestParam List<Integer> keeperIds) {
        return ResponseEntity.ok(
                courseRegistrationRequestKeeperService
                        .findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(keeperIds)
        );
    }
}
