package bg.sofia.uni.fmi.mjt.photoalbum;

import bg.sofia.uni.fmi.mjt.photoalbum.image.Image;
import bg.sofia.uni.fmi.mjt.photoalbum.image.ImageLoader;
import bg.sofia.uni.fmi.mjt.photoalbum.image.ImageProcessor;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {
    private int imageProcessorsCount;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        this.imageProcessorsCount = imageProcessorsCount;
    }

    @Override
    public void processImages(String sourceDirectory, String outputDirectory) {
        AtomicBoolean areAllLoaded = new AtomicBoolean(false);
        Queue<Image> loadedImages = new LinkedList<>();
        AtomicInteger loadedImagesCount = new AtomicInteger(0);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
                Path.of(sourceDirectory), "*.{png,jpg,jpeg}")
        ) {
            for (int i = 0; i < imageProcessorsCount; i++) {
                new Thread(
                        new ImageProcessor(
                                loadedImages,
                                loadedImagesCount,
                                areAllLoaded,
                                Path.of(outputDirectory)
                        )
                ).start();
            }

            directoryStream.forEach(path -> {
                new Thread(new ImageLoader(loadedImages, loadedImagesCount, path)).start();
            });

            areAllLoaded.set(true);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't open source file - " + sourceDirectory + "." , e);
        }
    }
}
