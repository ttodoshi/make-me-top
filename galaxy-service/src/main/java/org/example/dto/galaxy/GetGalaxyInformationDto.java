package org.example.dto.galaxy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dto.person.PersonWithSystemsDto;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GetGalaxyInformationDto extends GalaxyDto {
    private Integer galaxyId;
    private Integer systemCount;
    private Integer explorerCount;
    private Collection<PersonWithSystemsDto> explorers;
    private Integer keeperCount;
    private Collection<PersonWithSystemsDto> keepers;

    public GetGalaxyInformationDto(Integer galaxyId, String galaxyName, String galaxyDescription, Integer systemCount, Integer explorerCount, Collection<PersonWithSystemsDto> explorers, Integer keeperCount, Collection<PersonWithSystemsDto> keepers) {
        super(galaxyName, galaxyDescription);
        this.galaxyId = galaxyId;
        this.systemCount = systemCount;
        this.explorerCount = explorerCount;
        this.explorers = explorers;
        this.keeperCount = keeperCount;
        this.keepers = keepers;
    }
}
