package org.example.person.dto.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.example.person.utils.GetPersonDtoSerializer;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(using = GetPersonDtoSerializer.class)
public class GetPersonDto {
    private Long personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime registrationDate;
    private Integer maxExplorers;
    private String email;
    private String phoneNumber;
    private String skype;
    private String telegram;
    private Boolean isVisiblePrivateData;
}
