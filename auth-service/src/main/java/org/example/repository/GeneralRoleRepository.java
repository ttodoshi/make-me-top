package org.example.repository;

import org.example.model.GeneralRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface GeneralRoleRepository extends JpaRepository<GeneralRole, Integer> {
    @Query(value = "SELECT role.role_id, role.name FROM course.role\n" +
            "JOIN course.person_role ON person_role.role_id = role.role_id\n" +
            "WHERE person_role.person_id = ?1", nativeQuery = true)
    Set<GeneralRole> getRolesForPerson(Integer personId);
}
