package org.example.picture.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.grpc.PeopleService;
import org.example.picture.enums.PictureType;
import org.example.picture.service.PictureService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/pics")
@RequiredArgsConstructor
public class PictureController {
    private final PictureService pictureService;

    @GetMapping("/{picId}")
    @Operation(summary = "Get picture", tags = "pics")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested picture",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> findPicture(@PathVariable Long picId, @RequestParam PictureType type) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(pictureService.findPicture(picId, type));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Set picture", tags = "pics")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Picture set",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> setPicture(@AuthenticationPrincipal PeopleService.Person person, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(
                pictureService.savePicture(person.getPersonId(), file)
        );
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete picture", tags = "pics")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Picture deleted",
                    content = {
                            @Content(
                                    mediaType = "*")
                    })
    })
    public ResponseEntity<?> deletePicture(@AuthenticationPrincipal PeopleService.Person person) {
        pictureService.deletePicture(person.getPersonId());
        return ResponseEntity
                .noContent()
                .build();
    }
}
