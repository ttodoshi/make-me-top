package org.example.dto.explorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExplorerWithRatingDTO {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Double rating;
}
