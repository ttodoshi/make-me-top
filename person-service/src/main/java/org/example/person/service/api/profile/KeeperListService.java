package org.example.person.service.api.profile;

import org.example.person.dto.person.PersonWithGalaxiesDto;
import org.springframework.data.domain.Page;

public interface KeeperListService {
    Page<PersonWithGalaxiesDto> getKeepers(String authorizationHeader, Integer page, Integer size);
}
