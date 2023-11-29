package org.example.homework.dto.homework;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.dto.explorer.ExplorerBaseInfoDto;
import org.example.homework.model.HomeworkRequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkRequestWithPersonInfoDto {
    private Long requestId;
    private Long homeworkId;
    private ExplorerBaseInfoDto explorer;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime requestDate;
    private HomeworkRequestStatus status;
}
