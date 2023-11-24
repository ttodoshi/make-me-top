package org.example.homework.dto.homework;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworksWithRequestsDto {
    private List<GetHomeworkDto> activeHomeworks;
    private List<GetHomeworkDto> closedHomeworks;
}
