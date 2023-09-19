package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.HomeworkRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/homework-app")
public class HomeworkRequestController {
    private final HomeworkRequestService homeworkRequestService;

    @GetMapping("/homework-request")
    @PreAuthorize("isAuthenticated()") // TODO
//    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) && " +
//            "@roleService.hasAnyCourseRoleByExplorerIds(#explorerIds, T(org.example.config.security.role.CourseRoleType).KEEPER)")
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
