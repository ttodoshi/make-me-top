package org.example.dto.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonWithSystemsDto extends PersonWithRatingDto {
    private List<Integer> systems;

    public PersonWithSystemsDto(Integer personId, String firstName, String lastName, String patronymic, Double rating, List<Integer> systems) {
        super(personId, firstName, lastName, patronymic, rating);
        this.systems = systems;
    }
}
