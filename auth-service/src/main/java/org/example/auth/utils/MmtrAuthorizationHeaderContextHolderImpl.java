package org.example.auth.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Qualifier("mmtrAuthorizationHeaderContextHolder")
public class MmtrAuthorizationHeaderContextHolderImpl implements AuthorizationHeaderContextHolder {
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
