package bg.sofia.uni.fmi.mjt.photoalbum.image;

import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageLoader implements Runnable {
    private Path sourcePath;
    private Queue<Image> loadedImages;
    private AtomicInteger loadedImagesCount;

    public ImageLoader(Queue<Image> loadedImages, AtomicInteger loadedImagesCount, Path sourcePath) {
        this.loadedImages = loadedImages;
        this.sourcePath = sourcePath;
        this.loadedImagesCount = loadedImagesCount;
    }

    @Override
    public void run() {
        loadedImagesCount.incrementAndGet();
        Image image = Image.loadImage(sourcePath);

        synchronized (loadedImages) {
            loadedImages.add(image);
            loadedImages.notifyAll();
        }
    }
}
