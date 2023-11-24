package org.example.homework.dto.homework;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.dto.explorer.ExplorerBaseInfoDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkRequestVersionDto {
    private Integer versionId;
    private Integer requestId;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creationDate;
    private ExplorerBaseInfoDto explorer;
    private List<GetHomeworkRequestFeedbackDto> homeworkRequestFeedbacks;
}
