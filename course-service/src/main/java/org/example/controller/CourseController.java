package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.AddKeeperRequest;
import org.example.dto.CourseUpdateRequest;
import org.example.model.Course;
import org.example.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course-app/")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get course by course id", tags = "course")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course by courseId",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseById(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(courseService.getCourse(courseId));
    }

    @PostMapping("course")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }


    @PutMapping("course/{courseId}")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updateCourse(@RequestBody CourseUpdateRequest course,
                                          @PathVariable Integer courseId) {
        return ResponseEntity.ok(courseService.updateCourse(course, courseId));
    }

    @PostMapping("course/{courseId}/keeper")
    @PreAuthorize("@RoleService.hasAnyGeneralRole(T(org.example.model.GeneralRoleType).BIG_BROTHER)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added keeper",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setKeeperToCourse(@PathVariable("courseId") Integer courseId, @RequestBody AddKeeperRequest addKeeperRequest) {
        return ResponseEntity.ok(courseService.setKeeperToCourse(courseId, addKeeperRequest));
    }
}
