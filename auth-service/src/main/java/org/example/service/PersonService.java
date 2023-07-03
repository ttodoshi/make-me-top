package org.example.service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.config.mapper.PersonMapper;
import org.example.config.security.JwtServiceInterface;
import org.example.dto.AddKeeperRequest;
import org.example.dto.UserAuthResponse;
import org.example.dto.UserRequest;
import org.example.exception.classes.user.UserNotFoundException;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.repository.KeeperRepository;
import org.example.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final KeeperRepository keeperRepository;

    private final JwtServiceInterface jwtGenerator;

    private final PersonMapper personMapper;

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    @Value("${url_auth_mmtr}")
    private String mmtrAuthUrl;

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Object login(UserRequest request, HttpServletResponse response) {
        Person person;
        try {
            person = authenticatePerson(request);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new UserNotFoundException();
        }
        String token = jwtGenerator.generateToken(person, request.getRole());
        Cookie tokenCookie = generateCookie(token);
        response.addCookie(tokenCookie);
        return token;
    }

    private Person authenticatePerson(UserRequest request) {
        UserAuthResponse authResponse = sendAuthRequest(request)
                .orElseThrow(UserNotFoundException::new);
        Person person = personRepository.getPersonById(authResponse.getEmployeeId());
        if (person == null)
            return personRepository.save(personMapper.UserAuthResponseToPerson(authResponse));
        return person;
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

    public Keeper setKeeperToCourse(Integer courseId, AddKeeperRequest addKeeperRequest) {
        Keeper keeper = new Keeper();
        keeper.setCourseId(courseId);
        keeper.setPersonId(addKeeperRequest.getPersonId());
        keeper.setStartDate(new Date());
        return keeperRepository.save(keeper);
    }
}
