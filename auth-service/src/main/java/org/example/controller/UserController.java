package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserRequest;
import org.example.service.PersonService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PropertySource(value = {"classpath:config.properties"})
@RequiredArgsConstructor
public class UserController {
    private final PersonService personService;

    @PostMapping("login")
    @Operation(summary = "Log in", tags = "Authentication")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Log in by username and password successful",
                    content = {
                            @Content(
                                    mediaType = "text/plain")
                    })
    })
    public ResponseEntity<?> loginUser(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        return new ResponseEntity<>(personService.login(userRequest, response), HttpStatus.OK);
    }

    @PatchMapping("toKeeper/{personId}")
    @Secured("ROLE_BIG_BROTHER")
    @Operation(
            summary = "Update person role to keeper",
            tags = "For admin"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully changed person role to keeper",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> updatePersonRoleToKeeper(@PathVariable("personId") Integer personId) {
        return new ResponseEntity<>(personService.updatePersonRoleToKeeper(personId), HttpStatus.OK);
    }
}
