package org.example.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.feedback.service.CourseRatingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback-app")
@RequiredArgsConstructor
public class CourseRatingController {
    private final CourseRatingService courseRatingService;

    @GetMapping("/courses")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get rating", tags = "rating")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested rating",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> getCoursesRating(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                              @RequestParam List<Long> courseIds) {
        return ResponseEntity.ok(
                courseRatingService.getCoursesRating(authorizationHeader, courseIds)
        );
    }
}
