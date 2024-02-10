package org.example.person.mapper;

import com.google.protobuf.Timestamp;
import org.example.grpc.ExplorersService;
import org.example.person.model.Explorer;

import java.time.ZoneOffset;

public class ExplorerMapper {
    public static ExplorersService.Explorer mapExplorerToGrpcModel(Explorer explorer) {
        return ExplorersService.Explorer
                .newBuilder()
                .setExplorerId(explorer.getExplorerId())
                .setPersonId(explorer.getPersonId())
                .setGroupId(explorer.getGroupId())
                .setStartDate(
                        Timestamp.newBuilder()
                                .setSeconds(explorer.getStartDate().toEpochSecond(ZoneOffset.UTC))
                                .setNanos(explorer.getStartDate().getNano())
                                .build()
                ).build();
    }
}
