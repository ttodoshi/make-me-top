package org.example.config;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.repository.AuthorizationHeaderRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@RequiredArgsConstructor
public class AuthorizationHeaderAspectConfig {
    private final AuthorizationHeaderRepository authorizationHeaderRepository;

    @Pointcut("execution(public * org.example.controller.GalaxyController.getAllGalaxies(..))")
    public void callRestControllerPublic() {
    }

    @Before("callRestControllerPublic()")
    public void beforeCallRestControllerPublic() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeaderValue = request.getHeader("Authorization");
            authorizationHeaderRepository.setAuthorizationHeader(authorizationHeaderValue);
        }
    }
}
