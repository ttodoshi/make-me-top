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
@RequestMapping("/keeper-cabinet/homework")
@RequiredArgsConstructor
public class HomeworkRequestController {
    private final HomeworkRequestService homeworkRequestService;

    @PostMapping("{homeworkId}/mark")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Set homework mark", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework mark",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setHomeworkMark(@PathVariable("homeworkId") Integer homeworkId,
                                             @Valid @RequestBody MarkDTO mark) {
        return ResponseEntity.ok(homeworkRequestService.setHomeworkMark(homeworkId, mark));
    }

    @PostMapping("{homeworkId}/response")
    @PreAuthorize("@roleService.hasAnyCourseRoleByHomeworkId(#homeworkId, T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Send homework response", tags = "homework")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Homework response",
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