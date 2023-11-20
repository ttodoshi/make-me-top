package org.example.course.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerWithRatingDto extends ExplorerBaseInfoDto implements Comparable<ExplorerWithRatingDto> {
    private Double rating;

    @Override
    public int compareTo(ExplorerWithRatingDto explorerWithRatingDto) {
        return Double.compare(explorerWithRatingDto.getRating(), this.getRating());
    }
}
