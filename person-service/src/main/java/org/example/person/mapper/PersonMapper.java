package org.example.person.mapper;

import com.google.protobuf.Timestamp;
import org.example.grpc.PeopleService;
import org.example.person.model.Person;

import java.time.ZoneOffset;

public class PersonMapper {
    public static PeopleService.Person mapPersonToGrpcModel(Person person) {
        PeopleService.Person.Builder builder = PeopleService.Person.newBuilder()
                .setPersonId(person.getPersonId())
                .setFirstName(person.getFirstName())
                .setLastName(person.getLastName())
                .setPatronymic(person.getPatronymic())
                .setRegistrationDate(
                        Timestamp.newBuilder()
                                .setSeconds(person.getRegistrationDate().toEpochSecond(ZoneOffset.UTC))
                                .setNanos(person.getRegistrationDate().getNano())
                                .build()
                )
                .setMaxExplorers(person.getMaxExplorers())
                .setEmail(person.getEmail())
                .setIsVisiblePrivateData(person.getIsVisiblePrivateData());
        if (person.getPhoneNumber() != null) {
            builder = builder.setPhoneNumber(person.getPhoneNumber());
        }
        if (person.getSkype() != null) {
            builder = builder.setSkype(person.getSkype());
        }
        if (person.getTelegram() != null) {
            builder = builder.setTelegram(person.getTelegram());
        }
        return builder.build();
    }
}
