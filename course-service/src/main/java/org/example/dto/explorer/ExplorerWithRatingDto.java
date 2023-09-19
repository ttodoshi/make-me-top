package org.example.dto.explorer;

import lombok.*;

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
