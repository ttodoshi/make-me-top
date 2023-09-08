package org.example.dto.keeper;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KeeperWithRatingDto extends KeeperDto implements Comparable<KeeperWithRatingDto> {
    private Double rating;

    @Override
    public int compareTo(KeeperWithRatingDto keeperWithRatingDto) {
        return Double.compare(keeperWithRatingDto.getRating(), this.getRating());
    }
}
