package org.example.service;

import org.example.config.security.JwtService;
import org.example.config.security.role.RoleChecker;
import org.example.dto.LoginRequest;
import org.example.exception.classes.personEX.RoleNotAvailableException;
import org.example.model.Person;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final PersonService personService;
    private final JwtService jwtGenerator;
    private final Map<String, RoleChecker> roleCheckerMap;

    public AuthService(PersonService personService, JwtService jwtGenerator,
                       @Qualifier("roleCheckerMap") Map<String, RoleChecker> roleCheckerMap) {
        this.personService = personService;
        this.jwtGenerator = jwtGenerator;
        this.roleCheckerMap = roleCheckerMap;
    }

    public String login(LoginRequest request, HttpServletResponse response) {
        Person person = personService.authenticatePerson(request);
        if (!isRoleAvailable(person.getPersonId(), request.getRole()))
            throw new RoleNotAvailableException();
        String token = jwtGenerator.generateToken(person, request.getRole());
        Cookie tokenCookie = generateCookie(token);
        response.addCookie(tokenCookie);
        return token;
    }

    private boolean isRoleAvailable(Integer personId, String role) {
        return roleCheckerMap.containsKey(role) && roleCheckerMap.get(role).isRoleAvailable(personId);
    }

    private Cookie generateCookie(String token) {
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(43200);
        tokenCookie.setPath("/");
        return tokenCookie;
    }

    public Map<String, Object> logout(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("token", "");
        tokenCookie.setMaxAge(0);
        tokenCookie.setPath("/");
        response.addCookie(tokenCookie);
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("message", "Выход успешный");
        return jsonResponse;
    }
}
