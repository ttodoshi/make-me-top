package org.example.courseregistration.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.courseregistration.dto.courserequest.CreateKeeperRejectionDto;
import org.example.courseregistration.service.KeeperCourseRegistrationRequestService;
import org.example.courseregistration.service.KeeperRejectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/course-registration-app")
@RequiredArgsConstructor
public class KeeperCourseRegistrationRequestController {
    private final KeeperCourseRegistrationRequestService keeperCourseRegistrationRequestService;
    private final KeeperRejectionService keeperRejectionService;

    @PatchMapping("/course-requests/{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByRequestId(#requestId, T(org.example.courseregistration.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Approve course registration request", tags = "keeper course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Request closed",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> approveRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(
                keeperCourseRegistrationRequestService.approveRequest(requestId)
        );
    }

    @PostMapping("/course-requests/{requestId}/rejections")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByRequestId(#requestId, T(org.example.courseregistration.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Reject course registration request", tags = "keeper course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Request closed",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> rejectRequest(@PathVariable Long requestId,
                                           @Valid @RequestBody CreateKeeperRejectionDto rejection) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperCourseRegistrationRequestService.rejectRequest(requestId, rejection)
                );
    }

    @GetMapping("/course-requests/rejections")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get keeper rejection reasons", tags = "keeper course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Keeper rejection reasons",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getKeeperRejectionReasons() {
        return ResponseEntity.ok(keeperRejectionService.getRejectionReasons());
    }

    @GetMapping("/course-requests/approved")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get approved requests", tags = "keeper course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Approved requests by course id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getApprovedRequests(@RequestParam List<Long> keeperIds) {
        return ResponseEntity.ok(
                keeperCourseRegistrationRequestService.getApprovedRequests(keeperIds)
        );
    }

    @PostMapping("/courses/{courseId}/groups")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.courseregistration.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.courseregistration.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Start education on course", tags = "keeper course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Education started",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> startTeaching(@PathVariable Long courseId) {
        return ResponseEntity.ok(
                keeperCourseRegistrationRequestService.startTeaching(courseId)
        );
    }
}
