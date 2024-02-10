package org.example.course.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.course.dto.course.UpdateCourseDto;
import org.example.course.service.CourseService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
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
    public ResponseEntity<?> findCourseById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                            @CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                            @PathVariable Long courseId,
                                            @RequestParam(required = false) Boolean detailed) {
        if (detailed != null && detailed)
            return ResponseEntity.ok(
                    courseService.findCourseByCourseIdDetailed(authorizationHeader, authentication, courseId)
            );
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
    public ResponseEntity<?> findCoursesByCourseIdIn(@RequestParam List<Long> courseIds) {
        return ResponseEntity.ok(courseService.findCoursesByCourseIdIn(courseIds));
    }

    @PutMapping("/galaxies/{galaxyId}/courses/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(authentication.authorities, T(org.example.course.enums.AuthenticationRoleType).BIG_BROTHER)")
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
    public ResponseEntity<?> updateCourse(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                          @PathVariable Long galaxyId,
                                          @PathVariable Long courseId,
                                          @Valid @RequestBody UpdateCourseDto course) {
        return ResponseEntity.ok(
                courseService.updateCourse(authorizationHeader, galaxyId, courseId, course)
        );
    }
}
