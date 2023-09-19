package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.courserequest.CourseRegistrationRequestReplyDto;
import org.example.dto.courserequest.KeeperRejectionDto;
import org.example.service.KeeperCourseRegistrationRequestService;
import org.example.service.KeeperRejectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/course-registration-app/course-request")
@RequiredArgsConstructor
public class KeeperCourseRegistrationRequestController {
    private final KeeperCourseRegistrationRequestService keeperCourseRegistrationRequestService;
    private final KeeperRejectionService keeperRejectionService;

    @PatchMapping("/{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByRequestId(#requestId, T(org.example.config.security.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Reply to course registration request", tags = "keeper course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Request closed",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> replyToRequest(@PathVariable("requestId") Integer requestId,
                                            @Valid @RequestBody CourseRegistrationRequestReplyDto reply) {
        return ResponseEntity.ok(keeperCourseRegistrationRequestService.replyToRequest(requestId, reply));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.config.security.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Get approved requests by course id", tags = "keeper course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Approved requests by course id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getApprovedRequests(@PathVariable Integer courseId) {
        return ResponseEntity.ok(keeperCourseRegistrationRequestService.getApprovedRequests(courseId));
    }

    @PostMapping("/course/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.config.security.role.CourseRoleType).KEEPER)")
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
    public ResponseEntity<?> startTeaching(@PathVariable Integer courseId) {
        return ResponseEntity.ok(keeperCourseRegistrationRequestService.startTeaching(courseId));
    }

    @PostMapping("/{requestId}/rejection")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByRequestId(#requestId, T(org.example.config.security.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Send keeper rejection", tags = "keeper course request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Keeper rejection sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendKeeperRejection(@PathVariable("requestId") Integer requestId,
                                                 @Valid @RequestBody KeeperRejectionDto rejection) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperRejectionService.sendRejection(requestId, rejection)
                );
    }

    @GetMapping("/rejection")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER)")
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
}
