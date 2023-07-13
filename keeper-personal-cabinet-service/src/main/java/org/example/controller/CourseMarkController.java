package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.CourseMarkDTO;
import org.example.service.CourseMarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keeper-cabinet/mark")
@RequiredArgsConstructor
public class CourseMarkController {
    private final CourseMarkService courseMarkService;

    @PostMapping
    @PreAuthorize("@roleService.hasAnyCourseRoleByExplorerId(#courseMark.explorerId," +
            "T(org.example.model.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Set course mark from 1 to 5 to explorer", tags = "course mark")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Set course mark",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> sendRequest(@RequestBody CourseMarkDTO courseMark) {
        return ResponseEntity.ok(courseMarkService.setCourseMark(courseMark));
    }
}
