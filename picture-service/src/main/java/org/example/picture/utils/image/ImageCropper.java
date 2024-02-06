package org.example.picture.utils.image;

import org.example.picture.enums.PictureType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageCropper {
    public void crop(File outputDirectory, File picture, PictureType type, String extension) throws IOException {
        BufferedImage originalImage = ImageIO.read(picture);

        // crop a square portion from the original picture
        int squareSize = Math.min(originalImage.getWidth(), originalImage.getHeight());
        BufferedImage squaredImage = originalImage.getSubimage(
                0, 0, squareSize, squareSize
        );

        // create a new picture with the specified dimensions
        BufferedImage resized100Image = new BufferedImage(
                type.getWidth(), type.getHeight(), originalImage.getType()
        );
        Graphics2D g100 = resized100Image.createGraphics();
        g100.drawImage(
                squaredImage, 0, 0, type.getWidth(), type.getHeight(), null
        );
        g100.dispose();

        // save new picture
        String newFilename = String.format(
                "%s-%s.%s",
                StringUtils.stripFilenameExtension(picture.getName()), type.getName(), extension
        );
        ImageIO.write(resized100Image, extension, new File(outputDirectory, newFilename));
    }
}
