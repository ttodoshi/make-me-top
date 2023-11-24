package org.example.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.homework.service.HomeworkRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/homework-app")
public class HomeworkRequestController {
    private final HomeworkRequestService homeworkRequestService;

    @GetMapping("/homeworks/{homeworkId}/homework-requests")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).EXPLORER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.homework.enums.CourseRoleType).EXPLORER)")
    @Operation(summary = "Get homework with request", tags = "homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework with request",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findHomeworkWithRequestByHomeworkId(@PathVariable Integer homeworkId) {
        return ResponseEntity.ok(
                homeworkRequestService
                        .findHomeworkWithRequestByHomeworkId(homeworkId)
        );
    }

    @GetMapping("/homework-requests/{requestId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.homework.enums.AuthenticationRoleType).KEEPER) && " +
            "@roleService.hasAnyCourseRoleByHomeworkRequestId(#requestId, T(org.example.homework.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Get homework with request", tags = "homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework with request",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findHomeworkWithRequestByRequestId(@PathVariable Integer requestId) {
        return ResponseEntity.ok(
                homeworkRequestService.findHomeworkWithRequestByRequestId(requestId)
        );
    }

    @GetMapping("/homework-requests")
    @PreAuthorize("isAuthenticated()")
    // TODO used in keeper-profile(all good) and public explorer page (send if explorer in current group)
//    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.enums.AuthenticationRoleType).KEEPER) && " +
//            "@roleService.hasAnyCourseRoleByExplorerIds(#explorerIds, T(org.example.enums.CourseRoleType).KEEPER)")
    @Operation(summary = "Find opened homework requests by explorer id in", tags = "homework request")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework requests",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findOpenedHomeworkRequestsByExplorerIdIn(@RequestParam List<Integer> explorerIds) {
        return ResponseEntity.ok(homeworkRequestService.findOpenedHomeworkRequestsByExplorerIdIn(explorerIds));
    }
}
