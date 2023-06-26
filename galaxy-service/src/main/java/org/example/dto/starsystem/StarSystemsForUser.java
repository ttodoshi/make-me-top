package org.example.dto.starsystem;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class StarSystemsForUser {
    private String firstName;
    private String lastName;
    private String patronymic;
    private List<Integer> openedSystems;
    private List<SystemWithProgress> studiedSystems;
    private List<Integer> closedSystems;
}
