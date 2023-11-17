package org.example.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.springframework.security.access.AccessDeniedException;

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

    @GrpcExceptionHandler(AccessDeniedException.class)
    public StatusRuntimeException handleAccessDeniedException(Exception e) {
        logWarning(e);
        return Status.NOT_FOUND.withDescription("Вам закрыт доступ к данной функциональности бортового компьютера").asRuntimeException();
    }

    @GrpcExceptionHandler(ConnectException.class)
    public StatusRuntimeException handleConnectException(Exception e) {
        logWarning(e);
        return Status.INTERNAL.withDescription("Бортовой компьютер не смог связаться с внутренней системой данных").asRuntimeException();
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

    @GrpcExceptionHandler(KeeperNotFoundException.class)
    public StatusRuntimeException handleKeeperNotFoundException(Exception e) {
        logWarning(e);
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(ExplorerGroupNotFoundException.class)
    public StatusRuntimeException handleExplorerGroupNotFoundException(Exception e) {
        logWarning(e);
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }
}
