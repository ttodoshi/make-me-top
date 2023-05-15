package org.example.model.orbitModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.example.model.baseModel.OrbitBaseModel;
import org.example.model.systemModel.SystemCreateModel;

import java.util.List;

@JsonIgnoreProperties("galaxyId")
@Data
public class OrbitCreateWithOutGalaxyIdModel extends OrbitBaseModel {
    private List<SystemCreateModel> systemsList;
}
