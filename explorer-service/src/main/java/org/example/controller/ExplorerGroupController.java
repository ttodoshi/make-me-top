package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.CreateExplorerGroupDto;
import org.example.service.ExplorerGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/explorer-app")
@RequiredArgsConstructor
public class ExplorerGroupController {
    private final ExplorerGroupService explorerGroupService;

    @GetMapping("/group/{groupId}")
    @PreAuthorize("isAuthenticated()") // TODO
//    @PreAuthorize("(@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).EXPLORER) && " +
//            "@roleService.hasAnyCourseRoleByGroupId(#groupId, T(org.example.config.security.role.CourseRoleType).EXPLORER)) || " +
//            "(@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) && " +
//            "@roleService.hasAnyCourseRoleByGroupId(#groupId, T(org.example.config.security.role.CourseRoleType).KEEPER))")
    @Operation(summary = "Find group by id", tags = "group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested group",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findGroupById(@PathVariable Integer groupId) {
        return ResponseEntity.ok(explorerGroupService.findGroupById(groupId));
    }

    @GetMapping("/group")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find groups by keeper id in", tags = "group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested groups",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findGroupsByKeeperIdIn(@RequestParam List<Integer> keeperIds) {
        return ResponseEntity.ok(explorerGroupService.findGroupsByKeeperIdIn(keeperIds));
    }

    @GetMapping("/groups")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find groups course id by id in", tags = "group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Requested group",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> findExplorerGroupsCourseIdByGroupIdIn(@RequestParam List<Integer> groupIds) {
        return ResponseEntity.ok(explorerGroupService.findExplorerGroupsCourseIdByGroupIdIn(groupIds));
    }

    @PostMapping("/groups")
    @PreAuthorize("@roleService.hasAnyAuthenticationRole(T(org.example.config.security.role.AuthenticationRoleType).KEEPER) &&" +
            "@roleService.hasAnyCourseRole(#group.courseId, T(org.example.config.security.role.CourseRoleType).KEEPER)")
    @Operation(summary = "Create group", tags = "group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Group created",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public ResponseEntity<?> createExplorerGroup(@Valid @RequestBody CreateExplorerGroupDto group) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        explorerGroupService.createExplorerGroup(group)
                );
    }
}
