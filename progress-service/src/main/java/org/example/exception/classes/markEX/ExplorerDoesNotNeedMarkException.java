package org.example.exception.classes.markEX;

public class ExplorerDoesNotNeedMarkException extends RuntimeException {
    public ExplorerDoesNotNeedMarkException(Integer explorerId) {
        super("Исследователь " + explorerId + " не нуждается в итоговой оценке");
    }
}
