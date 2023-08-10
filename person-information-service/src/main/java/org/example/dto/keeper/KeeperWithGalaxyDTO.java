package org.example.dto.keeper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class KeeperWithGalaxyDTO {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Double rating;
    private Integer galaxyId;
    private String galaxyName;
    private List<Integer> systems;
}
