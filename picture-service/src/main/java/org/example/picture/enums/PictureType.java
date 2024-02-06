package org.example.picture.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PictureType {
    MINIATURE("min", 100, 100),
    NORMAL("normal", 400, 400);

    private final String name;
    private final Integer width;
    private final Integer height;
}
