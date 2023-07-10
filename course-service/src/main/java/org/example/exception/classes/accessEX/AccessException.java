package org.example.exception.classes.accessEX;

import org.springframework.security.access.AccessDeniedException;

public class AccessException extends AccessDeniedException {

    public AccessException() {
        super("Вам закрыт доступ к данной функциональности бортового компьютера");
    }
}
