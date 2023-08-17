package org.example.repository.courseregistration;

import org.example.dto.courseregistration.CourseRegistrationRequestForExplorer;
import org.example.dto.courseregistration.CourseRegistrationRequestForKeeper;
import org.example.dto.courseregistration.CourseRegistrationRequestForKeeperWithGalaxy;
import org.example.model.courserequest.CourseRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Integer> {
    @Query(value = "SELECT new org.example.dto.courseregistration.CourseRegistrationRequestForKeeper(crr.requestId, p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title, crr.keeperId)\n" +
            "FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN Course c ON c.courseId = crr.courseId\n" +
            "JOIN Keeper k ON k.keeperId = crr.keeperId\n" +
            "JOIN Person p ON p.personId = crr.personId\n" +
            "WHERE k.personId = :personId AND crrs.status = 'PROCESSING'")
    List<CourseRegistrationRequestForKeeper> getStudyRequestsByKeeperPersonId(@Param("personId") Integer personId);

    @Query(value = "SELECT new org.example.dto.courseregistration.CourseRegistrationRequestForKeeperWithGalaxy(crr.requestId, p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title, crr.keeperId)\n" +
            "FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN Course c ON c.courseId = crr.courseId\n" +
            "JOIN Person p ON p.personId = crr.personId\n" +
            "WHERE crr.personId = :personId AND crrs.status = 'PROCESSING'")
    Optional<CourseRegistrationRequestForKeeperWithGalaxy> getStudyRequestByPersonId(@Param("personId") Integer personId);

    @Query(value = "SELECT new org.example.dto.courseregistration.CourseRegistrationRequestForExplorer(" +
            "\tcrr.requestId, c.courseId, c.title, p.personId, p.firstName, p.lastName, p.patronymic, k.keeperId" +
            ") FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestStatus crrs ON crrs.statusId = crr.statusId\n" +
            "JOIN Course c ON c.courseId = crr.courseId\n" +
            "JOIN Keeper k ON k.keeperId = crr.keeperId\n" +
            "JOIN Person p ON p.personId = k.personId\n" +
            "WHERE crr.personId = :personId AND crrs.status = 'PROCESSING'")
    Optional<CourseRegistrationRequestForExplorer> getStudyRequestByExplorerPersonId(@Param("personId") Integer personId);
}
