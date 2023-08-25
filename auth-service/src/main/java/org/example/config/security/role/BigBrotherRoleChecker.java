package org.example.config.security.role;

import lombok.RequiredArgsConstructor;
import org.example.model.GeneralRole;
import org.example.model.GeneralRoleType;
import org.example.repository.GeneralRoleRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BigBrotherRoleChecker implements RoleChecker {
    private final GeneralRoleRepository generalRoleRepository;

    @Override
    public boolean isRoleAvailable(Integer personId) {
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
