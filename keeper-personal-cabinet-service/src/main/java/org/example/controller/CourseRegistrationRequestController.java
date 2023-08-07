package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.courseregistration.CourseRegistrationRequestReply;
import org.example.dto.courseregistration.KeeperRejectionDTO;
import org.example.service.CourseRegistrationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/keeper-cabinet/course-request")
@RequiredArgsConstructor
public class CourseRegistrationRequestController {
    private final CourseRegistrationRequestService courseRegistrationRequestService;

    @PatchMapping("{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER) || " +
            "@roleService.hasAnyCourseRoleByRequestId(#requestId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Reply to course registration request", tags = "course request")
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
                                            @Valid @RequestBody CourseRegistrationRequestReply reply) {
        return ResponseEntity.ok(courseRegistrationRequestService.replyToRequest(requestId, reply));
    }

    @PostMapping("{requestId}/rejection")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER) || " +
            "@roleService.hasAnyCourseRoleByRequestId(#requestId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Send keeper rejection", tags = "course request")
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
                                                 @Valid @RequestBody KeeperRejectionDTO rejection) {
        return ResponseEntity.ok(
                courseRegistrationRequestService.sendRejection(requestId, rejection));
    }

    @GetMapping("rejection")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER)")
    @Operation(summary = "Get keeper rejection reasons", tags = "course request")
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
        return ResponseEntity.ok(courseRegistrationRequestService.getRejectionReasons());
    }
}
