package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.person.UpdatePersonDto;
import org.example.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/person-app")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping("/person/{personId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find person by id", tags = "person")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested person",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findPersonById(@PathVariable Integer personId) {
        return ResponseEntity.ok(personService.findPersonById(personId));
    }

    @GetMapping("/person")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find people by person id in", tags = "person")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested person",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findPersonById(@RequestParam List<Integer> personIds) {
        return ResponseEntity.ok(personService.findPeopleByPersonIdIn(personIds));
    }

    @PatchMapping("/person/{personId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Set max explorers value", tags = "person")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Person updated",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> setMaxExplorersValueForPerson(@PathVariable Integer personId,
                                                           @Valid @RequestBody UpdatePersonDto person) {
        return ResponseEntity.ok(personService.setMaxExplorersValueForPerson(personId, person));
    }
}
