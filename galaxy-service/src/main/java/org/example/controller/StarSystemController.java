package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.model.modelDAO.StarSystem;
import org.example.model.systemModel.SystemCreateModel;
import org.example.model.systemModel.SystemWithDependencyModel;
import org.example.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/galaxy-app/system/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StarSystemController {

    @Autowired
    SystemService systemService;

    @GetMapping("{ID}")
    @Operation(summary = "get system by id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public SystemWithDependencyModel getSystemById(@PathVariable("ID") Integer id) {
        return systemService.getStartSystemById(id);
    }

    @PostMapping("create/{GALAXY_ID}")
    @Operation(summary = "create system", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void createSystem(@RequestBody SystemCreateModel model, @PathVariable("GALAXY_ID") Integer id) {
        systemService.createSystem(model, id);
    }


    @PutMapping("update/{GALAXY_ID}")
    @Operation(summary = "get system by id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void updateSystem(@RequestBody SystemCreateModel model, @PathVariable("GALAXY_ID") Integer id) {
        systemService.updateSystem(model, id);
    }

    @PutMapping("delete/{ID}")
    @Operation(summary = "get system by id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void deleteSystem(@PathVariable("ID") Integer id) {
        systemService.deleteSystem(id);
    }

    @GetMapping("byId/{ID}")
    @Operation(summary = "get system by id", tags = "system")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public StarSystem getSystemByIdWithOutDep(@PathVariable("ID") Integer id) {
        return systemService.getStartSystemByIdWithOutDependency(id);
    }
}
