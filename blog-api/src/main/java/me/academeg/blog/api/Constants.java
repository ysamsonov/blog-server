package me.academeg.blog.api;

import java.util.Optional;

/**
 * Constants
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
final public class Constants {
    public static final int DEFAULT_DATA_PAGE_SIZE = 20;

    public static final String AVATAR_PATH;

    public static final String IMAGE_PATH;

    public static final int MAX_THUMBNAIL_SIZE = 200;

    public static final String DATE_FORMAT = "YYYY-MM-dd'T'HH:mm:ss.SSSXXX";

    static {
        AVATAR_PATH = Optional
            .ofNullable(System.getProperty("me.academeg.blog.avatars.path"))
            .orElse("avatar/");

        IMAGE_PATH = Optional
            .ofNullable(System.getProperty("me.academeg.blog.images.path"))
            .orElse("image/");
    }

    private Constants() {
    }
}
