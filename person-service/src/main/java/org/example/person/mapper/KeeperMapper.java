package org.example.person.mapper;

import com.google.protobuf.Timestamp;
import org.example.grpc.KeepersService;
import org.example.person.model.Keeper;

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
}
