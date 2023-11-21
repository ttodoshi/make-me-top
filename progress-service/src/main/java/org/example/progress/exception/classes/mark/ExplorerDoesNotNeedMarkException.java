package org.example.progress.exception.classes.mark;

public class ExplorerDoesNotNeedMarkException extends RuntimeException {
    public ExplorerDoesNotNeedMarkException(Integer explorerId) {
        super("Исследователь " + explorerId + " не нуждается в итоговой оценке");
    }
}
