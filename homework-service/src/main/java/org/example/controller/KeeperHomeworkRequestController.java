package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.homework.CreateHomeworkFeedbackDto;
import org.example.dto.homework.HomeworkMarkDto;
import org.example.service.KeeperHomeworkRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/homework-app")
@RequiredArgsConstructor
public class KeeperHomeworkRequestController {
    private final KeeperHomeworkRequestService keeperHomeworkRequestService;

    @GetMapping("homework-request/{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkRequestId(#requestId, T(org.example.model.role.CourseRoleType).KEEPER)")
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

    @PostMapping("homework/{homeworkId}/mark")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
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
                                             @Valid @RequestBody HomeworkMarkDto mark) {
        return ResponseEntity.ok(keeperHomeworkRequestService.setHomeworkMark(homeworkId, mark));
    }

    @PostMapping("homework/{homeworkId}/feedback")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
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
                                                  @Valid @RequestBody CreateHomeworkFeedbackDto model) {
        return ResponseEntity.ok(keeperHomeworkRequestService.sendHomeworkFeedback(homeworkId, model));
    }
}
