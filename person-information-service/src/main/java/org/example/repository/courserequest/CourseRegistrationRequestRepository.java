package org.example.repository.courserequest;

import org.example.dto.courserequest.CourseRegistrationRequestForExplorerDto;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeperDto;
import org.example.dto.courserequest.CourseRegistrationRequestForKeeperWithGalaxyDto;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Integer> {
    @Query(value = "SELECT new org.example.dto.courserequest.CourseRegistrationRequestForKeeperDto(\n" +
            "   crr.requestId, p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title, crrk.keeperId\n" +
            ")\n" +
            "FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN CourseRegistrationRequestKeeper crrk ON crrk.requestId = crr.requestId\n" +
            "JOIN CourseRegistrationRequestKeeperStatus crrks ON crrks.statusId = crrk.statusId\n" +
            "JOIN Course c ON c.courseId = crr.courseId\n" +
            "JOIN Keeper k ON k.keeperId = crrk.keeperId\n" +
            "JOIN Person p ON p.personId = crr.personId\n" +
            "WHERE k.personId = :personId AND crrks.status = 'PROCESSING'")
    List<CourseRegistrationRequestForKeeperDto> getStudyRequestsByKeeperPersonId(@Param("personId") Integer personId);

    @Query(value = "SELECT new org.example.dto.courserequest.CourseRegistrationRequestForKeeperWithGalaxyDto(\n" +
            "   crr.requestId, p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title\n" +
            ")\n" +
            "FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN Course c ON c.courseId = crr.courseId\n" +
            "JOIN Person p ON p.personId = crr.personId\n" +
            "WHERE crr.personId = :personId AND crrs.status = 'PROCESSING'")
    Optional<CourseRegistrationRequestForKeeperWithGalaxyDto> getStudyRequestByPersonId(@Param("personId") Integer personId);

    @Query(value = "SELECT new org.example.dto.courserequest.CourseRegistrationRequestForExplorerDto(\n" +
            "   crr.requestId, c.courseId, c.title\n" +
            ") FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN Course c ON c.courseId = crr.courseId\n" +
            "WHERE crr.personId = :personId AND crrs.status = 'PROCESSING'")
    Optional<CourseRegistrationRequestForExplorerDto> getStudyRequestByExplorerPersonId(@Param("personId") Integer personId);
}
