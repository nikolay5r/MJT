package bg.sofia.uni.fmi.mjt.photoalbum.image;

import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageProcessor implements Runnable {
    private Queue<Image> loadedImages;
    private static AtomicInteger processedImagesCount;
    private AtomicInteger loadedImagesCount;
    private AtomicBoolean areAllImagesLoaded;
    private Path outputPath;

    public ImageProcessor(
            Queue<Image> loadedImages,
            AtomicInteger loadedImagesCount,
            AtomicBoolean areAllImagesLoaded,
            Path outputPath
    ) {
        processedImagesCount = new AtomicInteger(0);
        this.loadedImagesCount = loadedImagesCount;
        this.areAllImagesLoaded = areAllImagesLoaded;
        this.loadedImages = loadedImages;
        this.outputPath = outputPath;
    }

    @Override
    public void run() {
        synchronized (loadedImages) {
            try {
                while (true) {
                    while (loadedImages.isEmpty()) {
                        if (areAllImagesLoaded.get() && loadedImagesCount.get() == processedImagesCount.get()) {
                            return;
                        }

                        loadedImages.wait();
                    }
                    Image image = loadedImages.remove();
                    processedImagesCount.incrementAndGet();
                    image.convertToBlackAndWhite().saveImage(outputPath.toString());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Image processing was stopped and didn't finish.", e);
            }
        }
    }
}
