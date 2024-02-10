package org.example.person.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.example.person.exception.connect.ConnectException;
import org.example.person.exception.explorer.ExplorerGroupNotFoundException;
import org.example.person.exception.explorer.ExplorerNotFoundException;
import org.example.person.exception.keeper.KeeperNotFoundException;
import org.example.person.exception.person.PersonNotFoundException;
import org.springframework.security.access.AccessDeniedException;

@GrpcAdvice
public class GrpcExceptionAdvice {
    @GrpcExceptionHandler(AccessDeniedException.class)
    public StatusRuntimeException handleAccessDeniedException(Exception e) {
        return Status.NOT_FOUND.withDescription(
                "Вам закрыт доступ к данной функциональности бортового компьютера"
        ).asRuntimeException();
    }

    @GrpcExceptionHandler(ConnectException.class)
    public StatusRuntimeException handleConnectException(Exception e) {
        return Status.INTERNAL.withDescription(
                "Бортовой компьютер не смог связаться с внутренней системой данных"
        ).asRuntimeException();
    }

    @GrpcExceptionHandler(PersonNotFoundException.class)
    public StatusRuntimeException handlePersonNotFoundException(Exception e) {
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(ExplorerNotFoundException.class)
    public StatusRuntimeException handleExplorerNotFoundException(Exception e) {
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(KeeperNotFoundException.class)
    public StatusRuntimeException handleKeeperNotFoundException(Exception e) {
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }

    @GrpcExceptionHandler(ExplorerGroupNotFoundException.class)
    public StatusRuntimeException handleExplorerGroupNotFoundException(Exception e) {
        return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
    }
}
