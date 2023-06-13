package org.example.service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.config.JwtServiceInterface;
import org.example.exception.UserNotFoundException;
import org.example.model.Person;
import org.example.model.Role;
import org.example.model.UserAuthResponse;
import org.example.model.UserRequest;
import org.example.repository.PersonRepository;
import org.example.utils.PersonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;
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

    public String login(UserRequest request) {
        try {
            Person person = getPersonById(
                    personMapper.UserAuthResponseToPerson(
                            Objects.requireNonNull(
                                    sendAuthRequest(request)
                                            .orElseThrow(UserNotFoundException::new))
                    )
            );
            return jwtGenerator.generateToken(person);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new UserNotFoundException();
        }
    }

    @SneakyThrows
    public Optional<UserAuthResponse> sendAuthRequest(UserRequest userRequest) {
        okhttp3.RequestBody requestBody = okhttp3.RequestBody
                .create(JSON, userRequest.toString());
        var loginRequest = new Request.Builder()
                .url(mmtrAuthUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        Optional<UserAuthResponse> authResponseOptional = Optional.empty();
        try (var response = new OkHttpClient().newCall(loginRequest).execute()) {
            if (response.code() == HttpStatus.OK.value()) {
                String responseBody = response.body().string();
                if (isResponseSuccess(responseBody)) {
                    authResponseOptional = Optional.of(
                            new Gson()
                                    .fromJson(
                                            JsonParser.parseString(responseBody)
                                                    .getAsJsonObject()
                                                    .getAsJsonObject("object"),
                                            UserAuthResponse.class
                                    )
                    );
                }
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return authResponseOptional;
    }

    private boolean isResponseSuccess(String responseBody) {
        return JsonParser.parseString(responseBody)
                .getAsJsonObject()
                .get("isSuccess")
                .getAsBoolean();
    }

    private Person getPersonById(Person person) {
        Person reseivedPerson = personRepository.getPersonById(person.getPersonId());
        if (reseivedPerson == null)
            createNewPerson(person);
        return person;
    }

    private void createNewPerson(Person person) {
        person.setRole(Role.EXPLORER);
        personRepository.save(person);
    }

    public String updatePersonRoleToCurator(Integer personId) {
        try {
            Person person = personRepository.getReferenceById(personId);
            person.setRole(Role.KEEPER);
            personRepository.save(person);
            return "Роль успешно изменена";
        } catch (EntityNotFoundException e) {
            logger.severe(e.getMessage());
            throw new UserNotFoundException();
        }
    }
}
