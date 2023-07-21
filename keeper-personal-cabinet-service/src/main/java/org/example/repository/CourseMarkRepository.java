package org.example.repository;

import org.example.model.progress.CourseMark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseMarkRepository extends JpaRepository<CourseMark, Integer> {
}
