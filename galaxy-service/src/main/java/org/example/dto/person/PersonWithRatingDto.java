package org.example.dto.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonWithRatingDto {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Double rating;
}
