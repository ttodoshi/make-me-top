package org.example.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.homework.dto.homework.CreateHomeworkFeedbackDto;
import org.example.homework.dto.homework.CreateHomeworkMarkDto;
import org.example.homework.service.KeeperHomeworkRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/homework-app")
@RequiredArgsConstructor
public class KeeperHomeworkRequestController {
    private final KeeperHomeworkRequestService keeperHomeworkRequestService;

    @GetMapping("/homework-requests/{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkRequestId(#requestId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Get homework request", tags = "keeper homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework request",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getHomeworkRequest(@PathVariable("requestId") Integer requestId) {
        return ResponseEntity.ok(keeperHomeworkRequestService.getHomeworkRequest(requestId));
    }

    @PostMapping("/homeworks/{homeworkId}/marks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Set homework mark from 1 to 5", tags = "keeper homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework mark set",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setHomeworkMark(@PathVariable("homeworkId") Integer homeworkId,
                                             @Valid @RequestBody CreateHomeworkMarkDto mark) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperHomeworkRequestService.setHomeworkMark(homeworkId, mark)
                );
    }

    @PostMapping("/homeworks/{homeworkId}/feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Send homework feedback", tags = "keeper homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework feedback sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendHomeworkFeedback(@PathVariable("homeworkId") Integer homeworkId,
                                                  @Valid @RequestBody CreateHomeworkFeedbackDto feedback) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperHomeworkRequestService.sendHomeworkFeedback(homeworkId, feedback)
                );
    }
}
