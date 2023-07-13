package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.RoleService;
import org.example.model.AuthenticationRoleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class InformationService {
    private final RestTemplate restTemplate;
    private final RoleService roleService;

    @Value("${explorer_personal_cabinet_url}")
    private String EXPLORER_CABINET_URL;
    @Value("${keeper_personal_cabinet_url}")
    private String KEEPER_CABINET_URL;

    public ResponseEntity<?> getInformation(String token, HttpServletRequest request) {
        final String url;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            url = EXPLORER_CABINET_URL + request.getRequestURI();
        } else {
            url = KEEPER_CABINET_URL + request.getRequestURI();
        }
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Object.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity
                    .status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }
}
