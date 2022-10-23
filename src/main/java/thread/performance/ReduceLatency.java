package thread.performance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Improve latency by dividing the main task into subtasks
 * Process current task using 2 threads instead of 1 give us 1.5x decreased time consumption
 */
public class ReduceLatency {

    private static final String SOURCE_FILE = "./resources/image.jpeg";
    private static final String OUTPUT_FILE = "./resources/output_image.jpeg";

    public static void main(String[] args) throws Exception {
        File input = new File(SOURCE_FILE);
        BufferedImage originalImage = ImageIO.read(input);
        BufferedImage outputImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        long start = System.currentTimeMillis();
        recolorMultiThreaded(originalImage, outputImage, 2);
        long end = System.currentTimeMillis();

        System.out.printf("Time consumed: %s\n", end - start);

        File outputFile = new File(OUTPUT_FILE);
        ImageIO.write(outputImage, "jpg", outputFile);
    }

    public static void recolorMultiThreaded(BufferedImage original, BufferedImage output, int numOfThreads) throws InterruptedException {
        List<Thread> threads = new LinkedList<>();

        int width = original.getWidth();
        int height = original.getHeight() / numOfThreads;

        for (int i = 0; i < numOfThreads; i++) {
            final int threadMultiplier = i;

            threads.add(new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;

                recolorImage(original, output, leftCorner, topCorner, width, height);
            }));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    public static void recolorSingleThreaded(BufferedImage original, BufferedImage output) {
        recolorImage(original, output, 0, 0, original.getWidth(), original.getHeight());
    }

    public static void recolorImage(
            BufferedImage originalImage, BufferedImage outputImage, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, outputImage, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed = 0, newGreen = 0, newBlue = 0;

        if (isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newBlue = blue;
            newGreen = green;
        }
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int newRGB) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(newRGB, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        return rgb | 0xFF000000;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}
