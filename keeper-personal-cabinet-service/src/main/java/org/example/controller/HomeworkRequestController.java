package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.MarkDTO;
import org.example.dto.homework.CreateHomeworkResponse;
import org.example.service.HomeworkRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/keeper-cabinet")
@RequiredArgsConstructor
public class HomeworkRequestController {
    private final HomeworkRequestService homeworkRequestService;

    @GetMapping("homework-request/{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkRequestId(#requestId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Get homework request", tags = "homework")
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
        return ResponseEntity.ok(homeworkRequestService.getHomeworkRequest(requestId));
    }

    @PostMapping("homework/{homeworkId}/mark")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Set homework mark from 1 to 5", tags = "homework")
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
                                             @Valid @RequestBody MarkDTO mark) {
        return ResponseEntity.ok(homeworkRequestService.setHomeworkMark(homeworkId, mark));
    }

    @PostMapping("homework/{homeworkId}/response")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Send homework response", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework response sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendHomeworkResponse(@PathVariable("homeworkId") Integer homeworkId,
                                                  @Valid @RequestBody CreateHomeworkResponse model) {
        return ResponseEntity.ok(homeworkRequestService.sendHomeworkResponse(homeworkId, model));
    }
}
