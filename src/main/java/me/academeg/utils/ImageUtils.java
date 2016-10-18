package me.academeg.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

public class ImageUtils {

    private static int MAX_THUMBNAIL_SIZE = 200;

    public static String saveImage(File dir, MultipartFile file) {
        String fileName = UUID.randomUUID().toString();
        try (BufferedOutputStream outputStream =
                     new BufferedOutputStream(new FileOutputStream(new File(dir, fileName)));
             InputStream inputStream = file.getInputStream()) {
            byte[] cache = new byte[1024];
            int count;
            while ((count = inputStream.read(cache)) > 0) {
                outputStream.write(cache, 0, count);
            }
        } catch (Exception e) {
        }
        return fileName;
    }

    public static String compressImage(File originalImageFile, File dir) {
        String fileName = UUID.randomUUID().toString();
        try {
            BufferedImage originalImage = ImageIO.read(originalImageFile);
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            if (originalImage.getWidth() < MAX_THUMBNAIL_SIZE && originalImage.getHeight() < MAX_THUMBNAIL_SIZE) {
                return originalImageFile.getName();
            }

            BufferedImage resizeImagePng = resizeImage(originalImage, type);
            ImageIO.write(resizeImagePng, "png", new File(dir, fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
        int scale = Math.max(originalImage.getWidth(), originalImage.getHeight()) / MAX_THUMBNAIL_SIZE;
        int width = originalImage.getWidth() / scale;
        int height = originalImage.getHeight() / scale;

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    public static byte[] toByteArray(File file) {
        try (InputStream inputStream = new FileInputStream(file);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
