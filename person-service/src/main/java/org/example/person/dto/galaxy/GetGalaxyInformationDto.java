package org.example.person.dto.galaxy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.person.dto.person.PersonWithSystemsDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GetGalaxyInformationDto extends GalaxyDto {
    private Integer systemCount;
    private Integer explorerCount;
    private List<PersonWithSystemsDto> explorers;
    private Integer keeperCount;
    private List<PersonWithSystemsDto> keepers;
}
