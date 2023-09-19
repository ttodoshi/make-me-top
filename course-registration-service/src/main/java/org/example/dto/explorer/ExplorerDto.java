package org.example.dto.explorer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExplorerDto {
    private Integer explorerId;
    private Integer personId;
    private Integer groupId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;

    public ExplorerDto(Integer personId, Integer groupId) {
        this.personId = personId;
        this.groupId = groupId;
    }
}
