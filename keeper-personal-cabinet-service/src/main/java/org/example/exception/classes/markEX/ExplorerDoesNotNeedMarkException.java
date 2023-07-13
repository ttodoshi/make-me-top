package org.example.exception.classes.markEX;

public class ExplorerDoesNotNeedMarkException extends RuntimeException {
    public ExplorerDoesNotNeedMarkException() {
        super("Исследователь не нуждается в итоговой оценке");
    }
}
