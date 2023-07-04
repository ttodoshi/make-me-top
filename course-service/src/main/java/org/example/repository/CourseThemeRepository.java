package org.example.repository;

import org.example.model.CourseTheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseThemeRepository extends JpaRepository<CourseTheme, Integer> {
}
