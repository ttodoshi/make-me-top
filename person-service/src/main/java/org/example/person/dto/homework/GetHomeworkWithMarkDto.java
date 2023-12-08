package org.example.person.dto.homework;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetHomeworkWithMarkDto extends HomeworkDto {
    @Nullable
    private HomeworkRequestStatusDto status;
    @Nullable
    private HomeworkMarkDto mark;
}
