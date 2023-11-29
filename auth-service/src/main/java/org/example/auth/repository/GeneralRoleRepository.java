package org.example.auth.repository;

import org.example.auth.model.GeneralRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface GeneralRoleRepository extends JpaRepository<GeneralRole, Long> {
    @Query(value = "SELECT gr FROM PersonRole pr\n" +
            "JOIN GeneralRole gr ON gr.roleId = pr.roleId\n" +
            "WHERE pr.personId = :personId")
    Set<GeneralRole> getRolesForPerson(@Param("personId") Long personId);
}
