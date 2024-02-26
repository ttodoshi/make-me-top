package org.example.courseregistration.repository;

import org.example.courseregistration.model.CourseRegistrationRequestKeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestKeeperRepository extends JpaRepository<CourseRegistrationRequestKeeper, Long> {
    List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByRequestId(Long requestId);

    @Query("SELECT crrk FROM CourseRegistrationRequestKeeper crrk\n" +
            "WHERE crrk.keeperId IN :keeperIds AND crrk.status.status = 'PROCESSING'")
    List<CourseRegistrationRequestKeeper> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(@Param("keeperIds") List<Long> keeperIds);

    @Query(value = "SELECT crrk FROM CourseRegistrationRequest crr\n" +
            "JOIN CourseRegistrationRequestKeeper crrk ON crrk.requestId = crr.requestId\n" +
            "WHERE crrk.keeperId IN :keeperIds AND crr.status.status = 'APPROVED' AND crrk.status.status = 'APPROVED'\n" +
            "ORDER BY crrk.responseDate")
    List<CourseRegistrationRequestKeeper> findApprovedKeeperRequestsByKeeperIdIn(@Param("keeperIds") List<Long> keeperIds);

    Optional<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(Long requestId, Long keeperId);
}
