package org.example.dto.galaxy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dto.explorer.PersonWithSystemsDto;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GetGalaxyInformationDto extends GalaxyDto {
    private Integer systemCount;
    private Integer explorerCount;
    private Collection<PersonWithSystemsDto> explorers;
    private Integer keeperCount;
    private Collection<PersonWithSystemsDto> keepers;
}
