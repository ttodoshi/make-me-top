package org.example.course.dto.keeper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class KeeperWithRatingDto extends KeeperBaseInfoDto {
    private Double rating;

    public KeeperWithRatingDto(Long personId, String firstName, String lastName, String patronymic, Long keeperId, Double rating) {
        super(personId, firstName, lastName, patronymic, keeperId);
        this.rating = rating;
    }
}
