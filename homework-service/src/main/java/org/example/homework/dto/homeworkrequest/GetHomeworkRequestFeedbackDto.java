package org.example.homework.dto.homeworkrequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework.dto.keeper.KeeperBaseInfoDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkRequestFeedbackDto {
    private Long feedbackId;
    private Long requestVersionId;
    private String comment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creationDate;
    private KeeperBaseInfoDto keeper;
}
