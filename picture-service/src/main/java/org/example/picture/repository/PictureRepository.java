package org.example.picture.repository;

import org.example.picture.enums.PictureType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PictureRepository {
    Optional<byte[]> findPictureByPersonIdAndPictureType(Long personId, PictureType type);

    Long save(Long personId, MultipartFile file);

    void deleteByPersonId(Long personId);

}
