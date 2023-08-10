package org.example.dto.galaxy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dto.explorer.ExplorerWithSystemsDTO;
import org.example.dto.keeper.KeeperWithSystemsDTO;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GalaxyInformationGetResponse extends GalaxyDTO {
    private Integer systemCount;
    private Integer explorerCount;
    private Collection<ExplorerWithSystemsDTO> explorers;
    private Integer keeperCount;
    private Collection<KeeperWithSystemsDTO> keepers;

    public GalaxyInformationGetResponse(Integer galaxyId, String galaxyName, String galaxyDescription, Integer systemCount, Integer explorerCount, Collection<ExplorerWithSystemsDTO> explorers, Integer keeperCount, Collection<KeeperWithSystemsDTO> keepers) {
        super(galaxyId, galaxyName, galaxyDescription);
        this.systemCount = systemCount;
        this.explorerCount = explorerCount;
        this.explorers = explorers;
        this.keeperCount = keeperCount;
        this.keepers = keepers;
    }
}
