package org.example.auth.config.security.role;

import lombok.RequiredArgsConstructor;
import org.example.auth.model.GeneralRole;
import org.example.auth.model.GeneralRoleType;
import org.example.auth.repository.GeneralRoleRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BigBrotherRoleChecker implements RoleChecker {
    private final GeneralRoleRepository generalRoleRepository;

    @Override
    public boolean isRoleAvailable(Long personId) {
        for (GeneralRole role : generalRoleRepository.getRolesForPerson(personId)) {
            if (role.getName().equals(GeneralRoleType.BIG_BROTHER))
                return true;
        }
        return false;
    }

    @Override
    public String getType() {
        return GeneralRoleType.BIG_BROTHER.name();
    }
}
