package org.example.courseregistration.dto.courserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovedRequestDto {
    private Long requestId;
    private Long courseId;
    private Long personId;
    private Long statusId;
    private Long keeperId;
    private LocalDateTime responseDate;
}
