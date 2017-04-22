package me.academeg.blog.dal.utils;

import me.academeg.blog.api.Constants;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 * @date 08.02.2017
 */
public class ImageUtilsTest {

    private static final String IMAGE_PATH = "testImages/";
    private static final String BIG_IMAGE = "images/big_image.jpg";
    private static final String SMALL_IMAGE = "images/small_image.jpg";

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(IMAGE_PATH));
    }

    @Test
    public void saveCompressDeleteBigImage() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito
            .when(multipartFile.getInputStream())
            .thenReturn(getResourceAsStream(BIG_IMAGE));

        // original image
        String originalImageName = ImageUtils.saveImage(IMAGE_PATH, multipartFile);
        assertThat(new File(IMAGE_PATH + originalImageName).exists()).isTrue();
        assertThat(
            calculateMD5(new File(IMAGE_PATH + originalImageName))
        ).isEqualTo(
            calculateMD5ForResource(BIG_IMAGE)
        );

        // thumbnail image
        String thumbnailImageName = ImageUtils.compressImage(originalImageName, IMAGE_PATH);
        assertThat(new File(IMAGE_PATH + thumbnailImageName).exists()).isTrue();

        BufferedImage thumbnailBuffered = ImageIO.read(new File(IMAGE_PATH + thumbnailImageName));
        assertThat(thumbnailBuffered.getWidth() <= Constants.MAX_THUMBNAIL_SIZE).isTrue();
        assertThat(thumbnailBuffered.getHeight() <= Constants.MAX_THUMBNAIL_SIZE).isTrue();

        // get byte array
        byte[] originalByteArray = ImageUtils.toByteArray(new File(IMAGE_PATH + originalImageName));
        assertThat(originalByteArray).isNotNull();
        assertThat(originalByteArray.length).isGreaterThan(0);

        byte[] thumbnailByteArray = ImageUtils.toByteArray(new File(IMAGE_PATH + thumbnailImageName));
        assertThat(thumbnailByteArray).isNotNull();
        assertThat(thumbnailByteArray.length).isGreaterThan(0);

        // delete
        ImageUtils.deleteImages(IMAGE_PATH, originalImageName, thumbnailImageName);
        assertThat(new File(IMAGE_PATH + originalImageName).exists()).isFalse();
        assertThat(new File(IMAGE_PATH + thumbnailImageName).exists()).isFalse();
    }

    @Test
    public void saveCompressDeleteSmallImage() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito
            .when(multipartFile.getInputStream())
            .thenReturn(getResourceAsStream(SMALL_IMAGE));

        // original image
        String originalImageName = ImageUtils.saveImage(IMAGE_PATH, multipartFile);
        assertThat(new File(IMAGE_PATH + originalImageName).exists()).isTrue();
        assertThat(
            calculateMD5(new File(IMAGE_PATH + originalImageName))
        ).isEqualTo(
            calculateMD5ForResource(SMALL_IMAGE)
        );

        // thumbnail image
        String thumbnailImageName = ImageUtils.compressImage(originalImageName, IMAGE_PATH);
        assertThat(originalImageName.equals(thumbnailImageName)).isTrue();

        // get byte array
        byte[] originalByteArray = ImageUtils.toByteArray(new File(IMAGE_PATH + originalImageName));
        assertThat(originalByteArray).isNotNull();
        assertThat(originalByteArray.length).isGreaterThan(0);

        // delete
        ImageUtils.deleteImages(IMAGE_PATH, originalImageName);
        assertThat(new File(IMAGE_PATH + originalImageName).exists()).isFalse();
    }

    private InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }

    private String calculateMD5ForResource(String resourceName) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            return DigestUtils.md5DigestAsHex(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private String calculateMD5(File file) throws IOException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return DigestUtils.md5DigestAsHex(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
