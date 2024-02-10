package org.example.picture.repository.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.picture.enums.PictureType;
import org.example.picture.exception.picture.FileNotChangedException;
import org.example.picture.exception.picture.PictureNotFoundException;
import org.example.picture.exception.picture.PictureNotValidException;
import org.example.picture.repository.PictureRepository;
import org.example.picture.utils.image.ImageCropper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang.StringUtils.isBlank;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PictureRepositoryImpl implements PictureRepository {
    private final ImageCropper imageCropper;
    private final ResourceLoader resourceLoader;
    @Value("#{'${valid-pics-extensions}'.split(',')}")
    private Set<String> VALID_EXTENSIONS;
    @Value("${pics-directory}")
    private String PICS_DIR;

    @PostConstruct
    public void init() {
        Path picsDirectory = Paths.get(PICS_DIR);
        if (Files.notExists(picsDirectory)) {
            try {
                Files.createDirectory(picsDirectory);
            } catch (IOException e) {
                log.error("failed to create pics directory");
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Optional<byte[]> findPictureByPersonIdAndPictureType(Long personId, PictureType type) {
        try {
            Path personDirectory = findPersonDirectory(String.valueOf(personId));
            try (InputStream picture = findFile(personDirectory, personId, type.getName())) {
                return Optional.of(picture.readAllBytes());
            }
        } catch (IOException e) {
            log.error(e.toString());
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

    private InputStream findFile(Path path, Long personId, String type) throws IOException {
        try (Stream<Path> filesStream = Files.list(path)) {
            return filesStream
                    .filter(p -> StringUtils
                            .stripFilenameExtension(p.getFileName().toString())
                            .equals(String.format("%d-%s", personId, type)))
                    .findFirst()
                    .map(p -> {
                        try {
                            return Files.newInputStream(p);
                        } catch (IOException e) {
                            throw new PictureNotFoundException();
                        }
                    })
                    .orElseGet(() -> {
                        try {
                            return resourceLoader.getResource(
                                    String.format("classpath:default-%s.jpeg", type)
                            ).getInputStream();
                        } catch (IOException e) {
                            log.error("default picture not found in directory");
                            throw new PictureNotFoundException();
                        }
                    });
        }
    }

    @Override
    public Long save(Long personId, MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (isBlank(extension) || !VALID_EXTENSIONS.contains(extension)) {
            log.warn("picture extension not valid");
            throw new PictureNotValidException();
        }

        try {
            Path personDirectory = findPersonDirectory(String.valueOf(personId));
            clearDirectory(personDirectory);
            saveCroppedPictures(personDirectory, personId, file, extension);
        } catch (IOException e) {
            log.error(e.toString());
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
            log.error(e.toString());
            throw new FileNotChangedException();
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
