package org.example.dto.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.explorer.ExplorerDTO;
import org.example.dto.keeper.KeeperDTO;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithInfo {
    private Integer courseId;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date creationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date lastModified;
    private String description;
    private List<ExplorerDTO> explorers;
    private List<KeeperDTO> keepers;
}
