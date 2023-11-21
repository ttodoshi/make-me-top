package org.example.course.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.course.dto.course.UpdateCourseDto;
import org.example.course.service.CourseService;
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

    @GetMapping("/courses/{courseId}")
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
    public ResponseEntity<?> getCourseById(@PathVariable Integer courseId,
                                           @RequestParam(required = false) Boolean detailed) {
        if (detailed != null && detailed)
            return ResponseEntity.ok(courseService.findCourseByCourseIdDetailed(courseId));
        else
            return ResponseEntity.ok(courseService.findCourseByCourseId(courseId));
    }

    @GetMapping("/courses")
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

    @GetMapping("/galaxies/{galaxyId}/courses")
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

    @PutMapping("/galaxies/{galaxyId}/courses/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.course.enums.AuthenticationRoleType).BIG_BROTHER)")
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
