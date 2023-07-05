package org.example.repository;

import org.example.dto.CourseRegistrationRequestDTO;
import org.example.model.CourseRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Integer> {
    @Query(value = "SELECT new org.example.dto.CourseRegistrationRequestDTO(crr.requestId, p.personId, p.firstName, p.lastName, p.patronymic, c.courseId, c.title)\n" +
            "FROM CourseRegistrationRequest crr\n" +
            "JOIN Course c ON c.courseId = crr.courseId\n" +
            "JOIN Keeper k ON k.keeperId = crr.keeperId\n" +
            "JOIN Person p ON p.personId = crr.personId\n" +
            "WHERE k.personId = :personId")
    List<CourseRegistrationRequestDTO> getStudyRequestsByKeeperPersonId(@Param("personId") Integer personId);
}
