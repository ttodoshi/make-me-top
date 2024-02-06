package org.example.picture.repository.implementations;

import lombok.RequiredArgsConstructor;
import org.example.picture.enums.PictureType;
import org.example.picture.exception.classes.picture.FileNotChangedException;
import org.example.picture.exception.classes.picture.PictureNotFoundException;
import org.example.picture.exception.classes.picture.PictureNotValidException;
import org.example.picture.repository.PictureRepository;
import org.example.picture.utils.image.ImageCropper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class PictureRepositoryImpl implements PictureRepository {
    private final Set<String> VALID_EXTENSIONS = Set.of("png", "jpg", "jpeg");
    private final ImageCropper imageCropper;
    @Value("${pics-directory}")
    private String PICS_DIR;

    @PostConstruct
    public void init() throws IOException {
        Path picsDirectory = Paths.get(PICS_DIR);
        if (Files.notExists(picsDirectory)) {
            Files.createDirectory(picsDirectory);
        }
    }

    @Override
    public Optional<byte[]> findPictureByPersonIdAndPictureType(Long personId, PictureType type) {
        try {
            Path personDirectory = findPersonDirectory(String.valueOf(personId));
            Path picture = findFileByName(personDirectory, personId + "-" + type.getName());
            return Optional.of(
                    Files.readAllBytes(picture)
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Path findPersonDirectory(String personId) throws IOException {
        return createDirectoryIfNotExists(
                Paths.get(PICS_DIR, personId)
        );
    }

    private Path createDirectoryIfNotExists(Path directory) throws IOException {
        if (Files.notExists(directory)) {
            return Files.createDirectory(directory);
        }
        return directory;
    }

    private Path findFileByName(Path path, String filename) throws IOException {
        try (Stream<Path> filesStream = Files.list(path)) {
            return filesStream
                    .filter(p -> StringUtils
                            .stripFilenameExtension(p.getFileName().toString())
                            .equals(filename))
                    .findFirst()
                    .orElseThrow(PictureNotFoundException::new);
        }
    }

    @Override
    public Long save(Long personId, MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (isBlank(extension) || !VALID_EXTENSIONS.contains(extension)) {
            throw new PictureNotValidException();
        }

        try {
            Path personDirectory = findPersonDirectory(String.valueOf(personId));
            clearDirectory(personDirectory);
            saveCroppedPictures(personDirectory, personId, file, extension);
        } catch (IOException e) {
            throw new FileNotChangedException();
        }
        return personId;
    }

    private void saveCroppedPictures(Path personDirectory, Long personId, MultipartFile file, String extension) throws IOException {
        Path picturePath = personDirectory.resolve(
                personId + "." + extension
        );
        file.transferTo(picturePath);
        for (PictureType type : PictureType.values()) {
            imageCropper.crop(personDirectory.toFile(), picturePath.toFile(), type, extension);
        }
        Files.delete(picturePath);
    }

    @Override
    public void deleteByPersonId(Long personId) {
        try {
            clearDirectory(
                    findPersonDirectory(String.valueOf(personId))
            );
        } catch (IOException e) {
            throw new PictureNotFoundException();
        }
    }

    private void clearDirectory(Path directory) throws IOException {
        try (Stream<Path> files = Files.list(directory)) {
            Iterator<Path> iterator = files.iterator();
            while (iterator.hasNext()) {
                Files.delete(iterator.next());
            }
        }
    }
}
