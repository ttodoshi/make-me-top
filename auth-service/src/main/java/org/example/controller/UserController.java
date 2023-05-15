package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.config.JwtGeneratorInterface;
import org.example.exception.UserNotFoundException;
import org.example.model.Person;
import org.example.model.UserAuthResponse;
import org.example.model.UserRequest;
import org.example.sevice.PersonService;
import org.example.utils.PersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth/")
@PropertySource(value = {"classpath:config.properties"})
public class UserController {

    Logger logger = Logger.getLogger(UserController.class.getName());
    @Autowired
    private JwtGeneratorInterface jwtGenerator;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonMapper personMapper;

    @Value("${url_auth_mmtr}")
    String url_auth_mmtr;
    Person person;
    GsonBuilder builder;
    Gson gson;
    UserAuthResponse userAuthResponse;


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @PostMapping("login")
    public ResponseEntity<?> loginUser(@RequestBody UserRequest userRequest) {

        person = personService.checkPersonById(personMapper.UserAuthResponseToPerson(Objects.requireNonNull(checkExistsUser(userRequest))));
        try {
            return new ResponseEntity<>(jwtGenerator.generateToken(person), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("toCurator/{Id}")
    public void updatePersonToCurator(@PathVariable("Id") Integer personId) {
        personService.updatePersonToCurator(personId);
    }

    @SneakyThrows
    private UserAuthResponse checkExistsUser(UserRequest userRequest) {
        builder = new GsonBuilder();
        gson = builder.create();

        okhttp3.RequestBody bodyRequest = okhttp3.RequestBody.create(JSON, userRequest.toString());
        var getSystemById = new Request.Builder().post(bodyRequest).url(url_auth_mmtr).addHeader("content-type", "application/json").build();
        try (var response = new OkHttpClient().newCall(getSystemById).execute()) {
            if (response.code() == 200) {

                String bodyResponse = response.body().string();
                return new Gson().fromJson(JsonParser.parseString(bodyResponse).getAsJsonObject().getAsJsonObject("object"), UserAuthResponse.class);
            }
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
        return null;
    }

}
