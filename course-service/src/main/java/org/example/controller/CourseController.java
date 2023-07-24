package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseUpdateRequest;
import org.example.dto.keeper.AddKeeperRequest;
import org.example.model.Course;
import org.example.service.CourseService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course-app/")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get course by course id with keepers", tags = "course")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course by course id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseById(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseService.getCourse(courseId));
    }

    @GetMapping("galaxy/{galaxyId}/course")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all courses by galaxy id", tags = "course")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Courses",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCoursesByGalaxyId(@PathVariable("galaxyId") Integer galaxyId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token) {
        courseService.setToken(token);
        return ResponseEntity.ok(courseService.getCoursesByGalaxyId(galaxyId));
    }

    @PostMapping("course")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Create course", tags = "course", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createCourse(@Valid @RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }


    @PutMapping("galaxy/{galaxyId}/course/{courseId}")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Update course by id", tags = "course")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updateCourse(@PathVariable("galaxyId") Integer galaxyId,
                                          @PathVariable Integer courseId,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) @Schema(hidden = true) String token,
                                          @Valid @RequestBody CourseUpdateRequest course) {
        courseService.setToken(token);
        return ResponseEntity.ok(courseService.updateCourse(galaxyId, courseId, course));
    }

    @PostMapping("course/{courseId}/keeper")
    @PreAuthorize("@roleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @Operation(summary = "Add keeper on course", tags = "course")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added keeper",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setKeeperToCourse(@Valid @PathVariable("courseId") Integer courseId,
                                               @RequestBody AddKeeperRequest addKeeperRequest) {
        return ResponseEntity.ok(courseService.setKeeperToCourse(courseId, addKeeperRequest));
    }
}
