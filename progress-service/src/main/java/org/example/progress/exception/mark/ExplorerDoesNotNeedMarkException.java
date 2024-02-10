package org.example.progress.exception.mark;

public class ExplorerDoesNotNeedMarkException extends RuntimeException {
    public ExplorerDoesNotNeedMarkException(Long explorerId) {
        super("Исследователь " + explorerId + " не нуждается в итоговой оценке");
    }
}
