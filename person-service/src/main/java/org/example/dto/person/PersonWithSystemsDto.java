package org.example.dto.person;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class PersonWithSystemsDto {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Double rating;
    private List<Integer> systems;
}
