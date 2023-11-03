package org.example.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;

@GrpcAdvice
@Slf4j
public class GrpcExceptionAdvice {
    private void logWarning(Throwable e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement firstStackTraceElement = stackTrace[0];
            String className = firstStackTraceElement.getClassName();
            String methodName = firstStackTraceElement.getMethodName();
            int lineNumber = firstStackTraceElement.getLineNumber();
            log.warn("Произошла ошибка в классе: {}, методе: {}, строка: {}\n\n" + e + "\n", className, methodName, lineNumber);
        } else log.warn(e.toString());
    }

    @GrpcExceptionHandler(PersonNotFoundException.class)
    public StatusRuntimeException handlePersonNotFoundException(Exception e) {
        logWarning(e);
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(ExplorerNotFoundException.class)
    public StatusRuntimeException handleExplorerNotFoundException(Exception e) {
        logWarning(e);
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }
}
