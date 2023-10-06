package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.exception.classes.roleEX.RoleNotAvailableException;
import org.example.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback-app")
@RequiredArgsConstructor
public class PersonRatingController {
    private final RatingService ratingService;

    @GetMapping("/person/{personId}/rating")
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
    public ResponseEntity<?> getRating(@PathVariable("personId") Integer personId,
                                       @RequestParam String as) {
        if (as.equals("explorer"))
            return ResponseEntity.ok(ratingService.getPersonRatingAsExplorer(personId));
        else if (as.equals("keeper"))
            return ResponseEntity.ok(ratingService.getPersonRatingAsKeeper(personId));
        else throw new RoleNotAvailableException();
    }

    @GetMapping("/person/rating")
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
    public ResponseEntity<?> getPeopleRating(@RequestParam List<Integer> personIds,
                                             @RequestParam String as) {
        if (as.equals("explorer"))
            return ResponseEntity.ok(ratingService.getPeopleRatingAsExplorer(personIds));
        else if (as.equals("keeper"))
            return ResponseEntity.ok(ratingService.getPeopleRatingAsKeeper(personIds));
        else throw new RoleNotAvailableException();
    }
}
