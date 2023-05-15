package org.example.model.baseModel;

import lombok.Data;

@Data
public abstract class SystemBaseModel {
    private Integer systemId;
    private String systemName;
    private Integer systemLevel;
    private Integer orbitId;

    @Override
    public String toString() {
        return "SystemBaseModel{" +
                "systemId=" + systemId +
                ", systemName='" + systemName + '\'' +
                ", systemLevel=" + systemLevel +
                ", orbitId=" + orbitId +
                '}';
    }
}
