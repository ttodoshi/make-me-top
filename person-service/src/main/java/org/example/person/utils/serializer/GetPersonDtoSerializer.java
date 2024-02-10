package org.example.person.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.example.person.dto.person.GetPersonDto;

import java.io.IOException;

public class GetPersonDtoSerializer extends StdSerializer<GetPersonDto> {
    public GetPersonDtoSerializer() {
        this(null);
    }

    public GetPersonDtoSerializer(Class<GetPersonDto> t) {
        super(t);
    }

    @Override
    public void serialize(GetPersonDto dto, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("personId", dto.getPersonId());
        jsonGenerator.writeStringField("firstName", dto.getFirstName());
        jsonGenerator.writeStringField("lastName", dto.getLastName());
        jsonGenerator.writeStringField("patronymic", dto.getPatronymic());
        jsonGenerator.writeStringField("registrationDate", dto.getRegistrationDate().toString());
        jsonGenerator.writeNumberField("maxExplorers", dto.getMaxExplorers());
        jsonGenerator.writeStringField("email", dto.getEmail());
        if (dto.getSkype() != null) {
            jsonGenerator.writeStringField("skype", dto.getSkype());
        }
        if (dto.getTelegram() != null) {
            jsonGenerator.writeStringField("telegram", dto.getTelegram());
        }
        if (dto.getPhoneNumber() != null && dto.getIsVisiblePrivateData()) {
            jsonGenerator.writeStringField("phoneNumber", dto.getPhoneNumber());
        }

        jsonGenerator.writeEndObject();
    }
}