package org.example.person.service.api.profile;

import org.example.person.dto.person.PersonWithGalaxiesDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ExplorerListService {
    Page<PersonWithGalaxiesDto> getExplorers(String authorizationHeader, Integer page, Integer size);
}
