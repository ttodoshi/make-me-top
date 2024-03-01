package org.example.picture.repository;

import org.example.picture.enums.PictureType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PictureRepository {
    Optional<byte[]> findPictureByPersonIdAndPictureType(String id, PictureType type);

    String save(String id, MultipartFile file);

    void deleteById(String id);
}
