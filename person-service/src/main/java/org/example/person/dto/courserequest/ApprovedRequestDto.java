package org.example.person.dto.courserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovedRequestDto {
    private Integer requestId;
    private Integer courseId;
    private Integer personId;
    private Integer statusId;
    private Integer keeperId;
    private LocalDateTime responseDate;
}
