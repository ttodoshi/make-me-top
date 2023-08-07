package org.example.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
