package org.example.person.dto.courserequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseRegistrationRequestKeeperDto {
    private Long responseId;
    private Long requestId;
    private Long keeperId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime responseDate;
    private Long statusId;
}
