package org.example.repository;

import org.example.model.CourseRegistrationRequestKeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRegistrationRequestKeeperRepository extends JpaRepository<CourseRegistrationRequestKeeper, Integer> {
    List<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeepersByRequestId(Integer requestId);

    @Query("SELECT crrk FROM CourseRegistrationRequestKeeper crrk\n" +
            "WHERE crrk.keeperId IN :keeperIds AND crrk.status.status = 'PROCESSING'")
    List<CourseRegistrationRequestKeeper> findProcessingCourseRegistrationRequestKeepersByKeeperIdIn(@Param("keeperIds") List<Integer> keeperIds);

    Optional<CourseRegistrationRequestKeeper> findCourseRegistrationRequestKeeperByRequestIdAndKeeperId(Integer requestId, Integer keeperId);
}
