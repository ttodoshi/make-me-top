package org.example.homework.dto.homework;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetHomeworkWithRequestDto {
    private Long homeworkId;
    private String content;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GetHomeworkRequestWithVersionsDto request;
}
