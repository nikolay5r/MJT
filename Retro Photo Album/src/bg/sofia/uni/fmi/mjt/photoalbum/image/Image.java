package bg.sofia.uni.fmi.mjt.photoalbum.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Image {
    String name;
    BufferedImage data;

    public Image(String name, BufferedImage data) {
        this.name = name;
        this.data = data;
    }

    public static Image loadImage(Path imagePath) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath.toString()), e);
        }
    }

    public void saveImage(String destinationDirectory) {
        try {
            ImageIO.write(data, "png", new File(destinationDirectory, name));
            System.out.println("Saved " + name + " to " + destinationDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("While saving image %s", name), e);
        }
    }

    public Image convertToBlackAndWhite() {
        BufferedImage processedData = new BufferedImage(
                data.getWidth(), data.getHeight(), BufferedImage.TYPE_BYTE_GRAY
        );
        processedData.getGraphics().drawImage(data, 0, 0, null);

        return new Image(name, processedData);
    }
}
