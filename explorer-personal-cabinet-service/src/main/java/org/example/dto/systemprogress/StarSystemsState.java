package org.example.dto.systemprogress;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class StarSystemsState {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Set<Integer> openedSystems;
    private Set<SystemWithProgress> studiedSystems;
    private Set<Integer> closedSystems;
}
