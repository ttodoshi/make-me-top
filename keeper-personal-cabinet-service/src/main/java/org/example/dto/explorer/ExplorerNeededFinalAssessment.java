package org.example.dto.explorer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExplorerNeededFinalAssessment extends ExplorerRequest {
    private Integer explorerId;

    public ExplorerNeededFinalAssessment(Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String title, Integer explorerId) {
        super(personId, firstName, lastName, patronymic, courseId, title);
        this.explorerId = explorerId;
    }
}
