package org.example.progress.exception.classes.mark;

public class ExplorerDoesNotNeedMarkException extends RuntimeException {
    public ExplorerDoesNotNeedMarkException(Long explorerId) {
        super("Исследователь " + explorerId + " не нуждается в итоговой оценке");
    }
}
