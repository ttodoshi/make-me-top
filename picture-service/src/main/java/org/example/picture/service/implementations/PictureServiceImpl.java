package org.example.picture.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.picture.enums.PictureType;
import org.example.picture.exception.picture.PictureNotFoundException;
import org.example.picture.repository.PictureRepository;
import org.example.picture.service.PictureService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;

    @Override
    public byte[] findPicture(Long personId, PictureType pictureType) {
        return pictureRepository.findPictureByPersonIdAndPictureType(
                String.valueOf(personId), pictureType
        ).orElseThrow(PictureNotFoundException::new);
    }

    @Override
    public Long savePicture(Long personId, MultipartFile file) {
        return Long.valueOf(pictureRepository.save(
                String.valueOf(personId),
                file
        ));
    }

    @Override
    public void deletePicture(Long personId) {
        pictureRepository.deleteById(
                String.valueOf(personId)
        );
    }
}
