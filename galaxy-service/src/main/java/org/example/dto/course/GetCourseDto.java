package org.example.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.keeper.KeeperDto;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCourseDto {
    private CourseDto course;
    private List<ExplorerDto> explorers;
    private List<KeeperDto> keepers;
}
