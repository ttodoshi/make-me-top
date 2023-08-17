package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.service.CourseProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/explorer-cabinet/")
@RequiredArgsConstructor
public class CourseProgressController {
    private final CourseProgressService courseProgressService;

    @GetMapping("galaxy/{galaxyId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER)")
    @Operation(summary = "Get courses progress by galaxy id", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Galaxy courses progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getCoursesProgressForCurrentUser(@PathVariable("galaxyId") Integer galaxyId) {
        return ResponseEntity.ok(
                courseProgressService.getCoursesProgressForCurrentUser(galaxyId));
    }

    @GetMapping("course/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER) || " +
            "@roleService.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Get course themes progress by course id", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Themes progress",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> getSystemPlanetsProgress(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(
                courseProgressService.getThemesProgressByCourseId(courseId));
    }

    @DeleteMapping("course/{courseId}")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.model.role.AuthenticationRoleType).EXPLORER) || @roleService.hasAnyCourseRole(#courseId, T(org.example.model.role.CourseRoleType).EXPLORER)")
    @Operation(summary = "Leave course", tags = "system progress")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully left course",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> leaveCourse(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(
                courseProgressService.leaveCourse(courseId));
    }
}
