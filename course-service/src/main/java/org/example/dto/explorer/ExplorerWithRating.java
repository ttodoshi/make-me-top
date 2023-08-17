package org.example.dto.explorer;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerWithRating extends ExplorerDTO implements Comparable<ExplorerWithRating> {
    private Double rating;

    @Override
    public int compareTo(ExplorerWithRating explorerWithRating) {
        return Double.compare(explorerWithRating.getRating(), this.getRating());
    }
}
