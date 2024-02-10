package org.example.course.exception.galaxy;

public class GalaxyNotFoundException extends RuntimeException {
    public GalaxyNotFoundException(Long galaxyId) {
        super("Не удалось найти информацию о галактике " + galaxyId + " в памяти бортового компьютера");
    }
}
