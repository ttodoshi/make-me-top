package org.example.dto.keeper;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KeeperWithRating extends KeeperDTO implements Comparable<KeeperWithRating> {
    private Double rating;

    @Override
    public int compareTo(KeeperWithRating keeperWithRating) {
        return Double.compare(keeperWithRating.getRating(), this.getRating());
    }
}
