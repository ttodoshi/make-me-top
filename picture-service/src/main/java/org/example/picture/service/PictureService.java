package org.example.picture.service;

import org.example.picture.enums.PictureType;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService {
    byte[] findPicture(Long personId, PictureType pictureType);

    Long savePicture(Long personId, MultipartFile file);

    void deletePicture(Long personId);
}
