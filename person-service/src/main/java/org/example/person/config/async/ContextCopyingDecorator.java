package org.example.person.config.async;

import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

class ContextCopyingDecorator implements TaskDecorator {
    @NonNull
    @Override
    public Runnable decorate(@NonNull Runnable runnable) {
        RequestAttributes requestContext = RequestContextHolder.currentRequestAttributes();
        Authentication authenticationContext = SecurityContextHolder.getContext().getAuthentication();
        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(requestContext);
                SecurityContextHolder.getContext().setAuthentication(authenticationContext);
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
                SecurityContextHolder.clearContext();
            }
        };
    }
}