package org.example.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.example.dto.explorer.ExplorerDTO;
import org.example.dto.keeper.KeeperDTO;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseGetResponse {
    private CourseDTO course;
    private List<ExplorerDTO> explorers;
    private List<KeeperDTO> keepers;
}
