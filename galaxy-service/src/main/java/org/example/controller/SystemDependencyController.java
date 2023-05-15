package org.example.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.model.dependencyModel.CreateDependencyModel;
import org.example.model.dependencyModel.DeleteDependencyModel;
import org.example.service.DependencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/galaxy-app/dependency/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@JsonIgnoreProperties({"trace"})
public class SystemDependencyController {
    @Autowired
    DependencyService dependencyService;

    @PostMapping("createDependency")
    @Operation(summary = "add dependency", tags = "dependency")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void addDependency(@RequestBody List<CreateDependencyModel> dependency) {
        dependencyService.addDependency(dependency);
    }


    @DeleteMapping("delete")
    @Operation(summary = "delete dependency", tags = "dependency")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "system discovered",
                    content = {
                            @Content(
                                    mediaType = "application/json")
                    })
    })
    public void deleteDependency(@RequestBody DeleteDependencyModel model) {
        dependencyService.deleteDependency(model);
    }
}
