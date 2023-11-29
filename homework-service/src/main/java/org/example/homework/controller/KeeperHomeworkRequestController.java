package org.example.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.homework.dto.homework.CreateHomeworkMarkDto;
import org.example.homework.dto.homework.CreateHomeworkRequestFeedbackDto;
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

    @PostMapping("/homework-requests/{requestId}/marks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkRequestId(#requestId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
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
    public ResponseEntity<?> setHomeworkMark(@PathVariable Long requestId,
                                             @Valid @RequestBody CreateHomeworkMarkDto mark) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperHomeworkRequestService.setHomeworkMark(requestId, mark)
                );
    }

    @PostMapping("/homework-requests/{requestId}/feedbacks")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkRequestId(#requestId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Send homework request feedback", tags = "keeper homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework request feedback sent",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendHomeworkFeedback(@PathVariable Long requestId,
                                                  @Valid @RequestBody CreateHomeworkRequestFeedbackDto feedback) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        keeperHomeworkRequestService.sendHomeworkRequestFeedback(requestId, feedback)
                );
    }
}
