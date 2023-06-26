package org.example.service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.config.JwtServiceInterface;
import org.example.dto.UserAuthResponse;
import org.example.dto.UserRequest;
import org.example.exception.classes.user.UserNotFoundException;
import org.example.model.Person;
import org.example.model.Role;
import org.example.repository.PersonRepository;
import org.example.utils.PersonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    private final JwtServiceInterface jwtGenerator;

    private final PersonMapper personMapper;

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    @Value("${url_auth_mmtr}")
    String mmtrAuthUrl;

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String login(UserRequest request, HttpServletResponse response) {
        try {
            Person person = authenticatePerson(request);
            String token = jwtGenerator.generateToken(person);
            Cookie tokenCookie = generateCookie(token);
            response.addCookie(tokenCookie);
            return token;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new UserNotFoundException();
        }
    }

    private Person authenticatePerson(UserRequest request) {
        return getPerson(
                personMapper.UserAuthResponseToPerson(
                        sendAuthRequest(request)
                                .orElseThrow(UserNotFoundException::new)
                )
        );
    }

    private Cookie generateCookie(String token) {
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setMaxAge(43200);
        tokenCookie.setPath("/");
        return tokenCookie;
    }

    @SneakyThrows
    public Optional<UserAuthResponse> sendAuthRequest(UserRequest userRequest) {
        Request authRequest = createAuthRequest(userRequest);
        Optional<UserAuthResponse> authResponseOptional = Optional.empty();
        try (var response = new OkHttpClient().newCall(authRequest).execute()) {
            String responseBody = response.body().string();
            if (response.code() == HttpStatus.OK.value() && isResponseSuccess(responseBody))
                authResponseOptional = getUserInformation(responseBody);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return authResponseOptional;
    }

    private Request createAuthRequest(UserRequest userRequest) {
        okhttp3.RequestBody requestBody = okhttp3.RequestBody
                .create(JSON, userRequest.toString());
        return new Request.Builder()
                .url(mmtrAuthUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    private boolean isResponseSuccess(String responseBody) {
        return JsonParser.parseString(responseBody)
                .getAsJsonObject()
                .get("isSuccess")
                .getAsBoolean();
    }

    private Optional<UserAuthResponse> getUserInformation(String responseBody) {
        return Optional.of(
                new Gson().fromJson(
                        JsonParser.parseString(responseBody)
                                .getAsJsonObject()
                                .getAsJsonObject("object"),
                        UserAuthResponse.class
                )
        );
    }


    private Person getPerson(Person person) {
        Person reseivedPerson = personRepository.getPersonById(person.getPersonId());
        if (reseivedPerson == null)
            createNewPerson(person);
        return person;
    }

    private void createNewPerson(Person person) {
        person.setRole(Role.EXPLORER);
        personRepository.save(person);
    }

    public Map<String, String> updatePersonRoleToKeeper(Integer personId) {
        try {
            Person person = personRepository.getReferenceById(personId);
            person.setRole(Role.KEEPER);
            personRepository.save(person);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Теперь " + person.getFirstName() + " является хранителем");
            return response;
        } catch (EntityNotFoundException e) {
            logger.severe(e.getMessage());
            throw new UserNotFoundException();
        }
    }
}
