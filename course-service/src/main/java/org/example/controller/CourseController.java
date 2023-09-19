package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.course.UpdateCourseDto;
import org.example.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/course-app")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get course by course id", tags = "course")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested course",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCourseById(@PathVariable("courseId") Integer courseId,
                                           @RequestParam(required = false) Boolean detailed) {
        if (detailed != null && detailed)
            return ResponseEntity.ok(courseService.getDetailedCourseInfo(courseId));
        else
            return ResponseEntity.ok(courseService.getCourse(courseId));
    }

    @GetMapping("/course")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get courses by course id in", tags = "course")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested courses",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findCoursesByCourseIdIn(@RequestParam List<Integer> courseIds) {
        return ResponseEntity.ok(courseService.findCoursesByCourseIdIn(courseIds));
    }

    @GetMapping("/galaxy/{galaxyId}/course")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all courses by galaxy id", tags = "course")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested courses by galaxy id",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCoursesByGalaxyId(@PathVariable("galaxyId") Integer galaxyId) {
        return ResponseEntity.ok(courseService.getCoursesByGalaxyId(galaxyId));
    }

    @PutMapping("/galaxy/{galaxyId}/course/{courseId}")
    @PreAuthorize("@roleServiceImpl.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).BIG_BROTHER)")
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
                                          @Valid @RequestBody UpdateCourseDto course) {
        return ResponseEntity.ok(courseService.updateCourse(galaxyId, courseId, course));
    }
}
