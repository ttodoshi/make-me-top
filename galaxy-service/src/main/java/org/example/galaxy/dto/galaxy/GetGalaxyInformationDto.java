package org.example.galaxy.dto.galaxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.galaxy.dto.person.PersonWithSystemsDto;

import java.util.List;

@Data
@AllArgsConstructor
public class GetGalaxyInformationDto {
    private Long galaxyId;
    private String galaxyName;
    private String galaxyDescription;
    private Integer systemCount;
    private Integer explorerCount;
    private List<PersonWithSystemsDto> explorers;
    private Integer keeperCount;
    private List<PersonWithSystemsDto> keepers;
}
