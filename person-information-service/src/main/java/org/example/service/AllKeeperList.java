package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.keeper.KeeperWithGalaxyDTO;

import java.util.List;

@AllArgsConstructor
public class AllKeeperList implements KeeperList {
    protected List<KeeperWithGalaxyDTO> keepers;

    @Override
    public List<KeeperWithGalaxyDTO> getKeepers() {
        return keepers;
    }
}
