package org.example.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Qualifier("mmtrAuthorizationHeaderRepository")
public class MmtrAuthorizationHeaderRepositoryImpl implements AuthorizationHeaderRepository {
    @Override
    public String getAuthorizationHeader() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return (String) request.getAttribute("mmtrAuthorizationHeader");
        }
        return "";
    }

    @Override
    public void setAuthorizationHeader(String authorizationHeader) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute("mmtrAuthorizationHeader", authorizationHeader);
        }
    }
}
