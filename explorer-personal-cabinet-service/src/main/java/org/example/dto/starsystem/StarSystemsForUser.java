package org.example.dto.starsystem;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class StarSystemsForUser {
    private String firstName;
    private String lastName;
    private String patronymic;
    private Set<Integer> openedSystems;
    private Set<SystemWithProgress> studiedSystems;
    private Set<Integer> closedSystems;
}
