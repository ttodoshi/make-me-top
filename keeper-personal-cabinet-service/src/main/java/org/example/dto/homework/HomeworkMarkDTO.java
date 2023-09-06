package org.example.dto.homework;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.dto.coursemark.MarkDTO;

@Data
@EqualsAndHashCode(callSuper = true)
public class HomeworkMarkDTO extends MarkDTO {
    private String comment;
}
