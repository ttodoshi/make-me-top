package org.example.dto.keeper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class KeeperDto {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Double rating;
}
