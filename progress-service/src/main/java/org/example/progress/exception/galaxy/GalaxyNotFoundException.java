package org.example.progress.exception.galaxy;

public class GalaxyNotFoundException extends RuntimeException {
    public GalaxyNotFoundException(Long galaxyId) {
        super("Не удалось найти информацию о галактике " + galaxyId + " в памяти бортового компьютера");
    }
}
