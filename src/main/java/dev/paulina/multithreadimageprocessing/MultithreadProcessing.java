package dev.paulina.multithreadimageprocessing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MultithreadProcessing {

    private final ExecutorService executor;

    public MultithreadProcessing() {
        executor = Executors.newFixedThreadPool(4);
    }

    public BufferedImage processNegative(BufferedImage img) throws ExecutionException, InterruptedException {
        Callable<BufferedImage> task = () -> ImageProcessor.negative(img);
        Future<BufferedImage> future = executor.submit(task);
        return future.get();
    }

    public BufferedImage processThreshold(BufferedImage img, int threshold) throws ExecutionException, InterruptedException {
        Callable<BufferedImage> task = () -> ImageProcessor.threshold(img, threshold);
        Future<BufferedImage> future = executor.submit(task);
        return future.get();
    }

    public BufferedImage processEdgeDetection(BufferedImage img) throws ExecutionException, InterruptedException {
        Callable<BufferedImage> task = () -> ImageProcessor.edgeDetection(img);
        Future<BufferedImage> future = executor.submit(task);
        return future.get();
    }
}
