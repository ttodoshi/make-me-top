package org.example.dto.explorer;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "groupId"
)
public class ExplorerGroupDto {
    private Integer groupId;
    private Integer courseId;
    private Integer keeperId;
    @ToString.Exclude
    private List<ExplorerDto> explorers;
}
