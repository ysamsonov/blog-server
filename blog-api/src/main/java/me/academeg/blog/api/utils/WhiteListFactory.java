package me.academeg.blog.api.utils;

import org.jsoup.safety.Whitelist;

/**
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @since 07.03.2017
 */
public class WhiteListFactory {
    private WhiteListFactory() {
    }

    public static Whitelist getDefault() {
        return Whitelist.basicWithImages();
    }

    public static Whitelist getDefaultWithVideos() {
        return getDefault().addAttributes("iframe", "width", "height", "src", "class", "frameborder");
    }
}
