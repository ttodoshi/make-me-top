package org.example.person.mapper;

import com.google.protobuf.Timestamp;
import org.example.grpc.KeepersService;
import org.example.person.dto.keeper.KeeperBasicInfoDto;
import org.example.person.model.Keeper;
import org.example.person.model.Person;

import java.time.ZoneOffset;

public class KeeperMapper {
    public static KeepersService.Keeper mapKeeperToGrpcModel(Keeper keeper) {
        return KeepersService.Keeper
                .newBuilder()
                .setKeeperId(keeper.getKeeperId())
                .setPersonId(keeper.getPersonId())
                .setCourseId(keeper.getCourseId())
                .setStartDate(
                        Timestamp.newBuilder()
                                .setSeconds(keeper.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                .setNanos(keeper.getStartDate().getNano())
                                .build()
                ).build();
    }

    public static KeeperBasicInfoDto mapKeeperToKeeperBasicInfoDto(Keeper keeper) {
        return new KeeperBasicInfoDto(
                keeper.getPerson().getPersonId(),
                keeper.getPerson().getFirstName(),
                keeper.getPerson().getLastName(),
                keeper.getPerson().getPatronymic(),
                keeper.getKeeperId()
        );
    }
}
