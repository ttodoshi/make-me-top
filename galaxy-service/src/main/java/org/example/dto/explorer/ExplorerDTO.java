package org.example.dto.explorer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class ExplorerDTO {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Double rating;
}
