package org.example.repository.courseregistration;

import org.example.model.courserequest.CourseRegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRegistrationRequestRepository extends JpaRepository<CourseRegistrationRequest, Integer> {
}
