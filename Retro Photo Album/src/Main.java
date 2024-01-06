import bg.sofia.uni.fmi.mjt.photoalbum.ParallelMonochromeAlbumCreator;

public class Main {
    public static void main(String[] args) {
        ParallelMonochromeAlbumCreator albumCreator = new ParallelMonochromeAlbumCreator(10);
        albumCreator.processImages("E:\\Photos\\java", "E:\\Photos\\java2");
    }
}