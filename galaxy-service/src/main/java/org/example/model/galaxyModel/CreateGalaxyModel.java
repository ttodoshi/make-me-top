package org.example.model.galaxyModel;

import lombok.Data;
import org.example.model.baseModel.GalaxyBaseModel;
import org.example.model.orbitModel.OrbitCreateWithOutGalaxyIdModel;

import java.util.List;

@Data

public class CreateGalaxyModel extends GalaxyBaseModel {
    List<OrbitCreateWithOutGalaxyIdModel> orbitsList;
}
