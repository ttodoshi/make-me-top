package org.example.course.dto.keeper;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KeeperWithRatingDto extends KeeperBaseInfoDto implements Comparable<KeeperWithRatingDto> {
    private Double rating;

    @Override
    public int compareTo(KeeperWithRatingDto keeperWithRatingDto) {
        return Double.compare(keeperWithRatingDto.getRating(), this.getRating());
    }
}
