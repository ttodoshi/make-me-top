package org.example.dto.galaxy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dto.keeper.KeeperDTO;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GalaxyInformationGetResponse extends GalaxyDTO {
    private Integer galaxyId;
    private Integer systemCount;
    private Integer explorerCount;
    private Integer keeperCount;
    private Collection<KeeperDTO> keepers;

    public GalaxyInformationGetResponse(Integer galaxyId, String galaxyName, String galaxyDescription, Integer systemCount, Integer explorerCount, Integer keeperCount, Collection<KeeperDTO> keepers) {
        super(galaxyName, galaxyDescription);
        this.galaxyId = galaxyId;
        this.systemCount = systemCount;
        this.explorerCount = explorerCount;
        this.keeperCount = keeperCount;
        this.keepers = keepers;
    }
}
