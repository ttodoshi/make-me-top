package org.example.course.dto.explorer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExplorerWithRatingDto extends ExplorerBaseInfoDto {
    private Double rating;

    public ExplorerWithRatingDto(Long personId, String firstName, String lastName, String patronymic, Long explorerId, Double rating) {
        super(personId, firstName, lastName, patronymic, explorerId);
        this.rating = rating;
    }
}
